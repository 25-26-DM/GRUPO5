package ec.edu.uce.book.data.repository

import android.content.Context
import android.net.Uri
import ec.edu.uce.book.data.dao.ProductDao
import ec.edu.uce.book.data.entity.ProductEntity
import ec.edu.uce.book.data.entity.toDynamoModel
import ec.edu.uce.book.data.entity.toEntity
import ec.edu.uce.book.data.remote.DynamoDBHelper
import ec.edu.uce.book.data.remote.S3Helper
import ec.edu.uce.book.util.NetworkUtils
import kotlinx.coroutines.flow.Flow
import java.security.interfaces.RSAMultiPrimePrivateCrtKey

class ProductRepository(
    private val productDao: ProductDao,
    private val context: Context,
    private val dynamoDBHelper: DynamoDBHelper,
    private val s3Helper: S3Helper
) {
    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun addProduct(product: ProductEntity): Boolean {

        val ts = System.currentTimeMillis()

        var finalPhotoUri = product.photoUri

        // 🔥 SUBIR IMAGEN SI ES LOCAL
        if (
            NetworkUtils.isInternetAvailable(context) &&
            product.photoUri?.startsWith("content://") == true
        ) {

            val uploadedUrl = try {
                s3Helper.uploadImage(
                    Uri.parse(product.photoUri)
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (uploadedUrl != null) {
                finalPhotoUri = uploadedUrl
            }
        }

        val productWithUrl = product.copy(
            photoUri = finalPhotoUri,
            lastModified = ts,
            synced = false,
            pendingAction = "UPSERT"
        )

        // 1️⃣ Guardar LOCAL (ya con URL si se pudo subir)
        val newId = productDao.insert(productWithUrl).toInt()

        // 2️⃣ Subir a Dynamo
        return if (NetworkUtils.isInternetAvailable(context)) {

            val ok = dynamoDBHelper.saveProduct(
                productWithUrl.copy(
                    id = newId,
                    lastModified = ts
                ).toDynamoModel()
            )

            if (ok) productDao.markAsSynced(newId)

            pullFromDynamoToLocal()

            ok
        } else {
            false
        }
    }


    suspend fun updateProduct(product: ProductEntity): Boolean {

        val ts = System.currentTimeMillis()

        var finalPhotoUri = product.photoUri

        if (
            NetworkUtils.isInternetAvailable(context) &&
            product.photoUri?.startsWith("content://") == true
        ) {

            val uploadedUrl = s3Helper.uploadImage(
                Uri.parse(product.photoUri)
            )

            if (uploadedUrl != null) {
                finalPhotoUri = uploadedUrl
            }
        }

        val updated = product.copy(
            photoUri = finalPhotoUri,
            lastModified = ts,
            synced = false,
            pendingAction = "UPSERT"
        )

        productDao.update(updated)

        return if (NetworkUtils.isInternetAvailable(context)) {

            val ok = dynamoDBHelper.saveProduct(
                updated.toDynamoModel()
            )

            if (ok) productDao.markAsSynced(product.id)

            pullFromDynamoToLocal()

            ok
        } else {
            false
        }
    }


    suspend fun deleteProduct(product: ProductEntity): Boolean {
        val ts = System.currentTimeMillis()

        // Si NO hay internet: NO borres el registro, márcalo como DELETE pendiente
        if (!NetworkUtils.isInternetAvailable(context)) {
            productDao.markAsPending(product.id, "DELETE", ts)
            return false
        }

        // Si hay internet: borra en Dynamo, y luego borra local
        val ok = dynamoDBHelper.deleteProduct(product.id.toString())
        if (ok) {
            // Borra local para que desaparezca al instante
            productDao.delete(product)
        } else {
            // Si falla, deja marcado para reintentar luego
            productDao.markAsPending(product.id, "DELETE", ts)
        }

        // refresca Room desde Dynamo (por si hay diferencias)
        pullFromDynamoToLocal()

        return ok
    }

    /**
     * ✅ Sube a DynamoDB todo lo pendiente (UPSERT/DELETE) cuando haya internet
     * y luego ✅ baja desde Dynamo a Room para que se vean TODOS en la app.
     *
     * Retorna cuántos cambios pendientes se lograron subir.
     */
    suspend fun syncPending(): Int {
        if (!NetworkUtils.isInternetAvailable(context)) return 0

        val pendientes = productDao.getPendingProducts()
        var syncedCount = 0

        for (p in pendientes) {
            val ok = try {
                when (p.pendingAction) {
                    "DELETE" -> dynamoDBHelper.deleteProduct(p.id.toString())
                    else -> dynamoDBHelper.saveProduct(p.toDynamoModel()) // UPSERT
                }
            } catch (e: Exception) {
                false
            }

            if (ok) {
                productDao.markAsSynced(p.id)
                syncedCount++
            }
        }

        // Limpia los deletes que ya se sincronizaron
        productDao.deleteSyncedDeletes()

        // ✅ CLAVE: ahora bajamos Dynamo -> Room (para ver los 6)
        pullFromDynamoToLocal()

        return syncedCount
    }


    suspend fun pullFromCloud(): Int {
        if (!NetworkUtils.isInternetAvailable(context)) return 0

        val cloudProducts = dynamoDBHelper.getAllProducts()
        var merged = 0

        for (remote in cloudProducts) {
            val cloudEntity = remote.toEntity()

            val local = productDao.getByCode(cloudEntity.code)

            if (local == null) {
                // Insertar nuevo desde nube
                productDao.insert(
                    cloudEntity.copy(
                        id = 0,
                        synced = true,
                        pendingAction = "",
                        lastModified = remote.lastModified
                    )
                )
                merged++
            } else {
                val needsUpdate = remote.lastModified > local.lastModified

                if (needsUpdate && local.pendingAction != "DELETE") {
                    productDao.update(
                        cloudEntity.copy(
                            id = local.id,
                            synced = true,
                            pendingAction = "",
                            lastModified = remote.lastModified
                        )
                    )
                    merged++
                }
            }
        }

        return merged
    }


    /**
     * ✅ Baja TODO lo de DynamoDB y lo “mergea” a Room.
     * Reglas:
     * - Busca por code (porque el id local puede variar)
     * - Si no existe en Room -> lo inserta
     * - Si existe -> actualiza SOLO si Dynamo está más nuevo (lastModified)
     */
    suspend fun pullFromDynamoToLocal(): Int {
        if (!NetworkUtils.isInternetAvailable(context)) return 0

        val remoteList = try {
            dynamoDBHelper.getAllProducts()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }

        println("DYNAMO -> remoteList size = ${remoteList.size}")



        var merged = 0

        for (remote in remoteList) {

            val remoteEntity = remote.toEntity()

            val local = productDao.getByCode(remoteEntity.code)

            if (local == null) {
                // ➕ Nuevo desde Dynamo
                productDao.insert(
                    remoteEntity.copy(
                        id = 0,                 // Room autogenera
                        synced = true,
                        pendingAction = "",
                        lastModified = remote.lastModified
                    )
                )
                merged++
            } else {
                // 🔄 Actualizar solo si Dynamo es más reciente
                if (
                    remote.lastModified > local.lastModified &&
                    local.pendingAction != "DELETE"
                ) {
                    productDao.update(
                        remoteEntity.copy(
                            id = local.id,
                            synced = true,
                            pendingAction = "",
                            lastModified = remote.lastModified
                        )
                    )
                    merged++
                }
            }
        }

        return merged
    }

}
