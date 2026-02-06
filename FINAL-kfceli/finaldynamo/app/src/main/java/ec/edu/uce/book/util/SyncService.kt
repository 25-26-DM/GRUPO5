package ec.edu.uce.book.util

import android.content.Context
import ec.edu.uce.book.data.dao.ProductDao
import ec.edu.uce.book.data.entity.ProductEntity
import ec.edu.uce.book.data.entity.toEntity
import ec.edu.uce.book.data.remote.DynamoDBHelper
import ec.edu.uce.book.data.repository.ProductRepository

class SyncService(
    private val context: Context,
    private val productRepository: ProductRepository,
    private val productDao: ProductDao,
    private val dynamoDBHelper: DynamoDBHelper
) {

    /**
     * Sincronización completa:
     * 1) Sube pendientes (UPSERT/DELETE) -> usando el repo
     * 2) Descarga de Dynamo -> inserta/actualiza en Room por CODE
     */
    suspend fun performFullSync(): SyncResult {
        if (!NetworkUtils.isInternetAvailable(context)) {
            return SyncResult.NoInternet
        }

        return try {
            // 1) Subir SOLO lo pendiente (usa tu lógica de pendingAction)
            val uploaded = try {
                productRepository.syncPending()
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }

            // 2) Bajar desde Dynamo y merge a local
            val downloadResult = downloadFromCloud()

            when (downloadResult) {
                is SyncResult.Success -> {
                    SyncResult.Success(
                        uploaded = uploaded,
                        downloaded = downloadResult.downloaded,
                        failed = downloadResult.failed
                    )
                }
                is SyncResult.Error -> downloadResult
                SyncResult.NoInternet -> SyncResult.NoInternet
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult.Error(e.message ?: "Error en sincronización")
        }
    }

    /**
     * Descarga de DynamoDB a Room
     * - Clave natural: CODE
     * - Inserta si no existe
     * - Actualiza si Dynamo es más nuevo
     * - NO pisa cambios locales pendientes (synced=false / pendingAction UPSERT/DELETE)
     */
    private suspend fun downloadFromCloud(): SyncResult {
        return try {
            val cloudProducts = dynamoDBHelper.getAllProducts()
            var downloaded = 0
            var failed = 0

            for (remote in cloudProducts) {
                try {
                    val cloudEntity = remote.toEntity()
                    val local = productDao.getByCode(cloudEntity.code)

                    if (local == null) {
                        // No existe local -> insertar como sincronizado
                        productDao.insert(
                            cloudEntity.copy(
                                id = 0,                  // Room autogenera
                                synced = true,
                                pendingAction = "UPSERT", // o "" si prefieres, pero evita null
                                lastModified = remote.lastModified
                            )
                        )
                        // Si usas pendingAction "" en tu DAO, cámbialo aquí también:
                        productDao.markAsSyncedByCode(cloudEntity.code) // (opcional si lo creas)
                        downloaded++
                    } else {
                        // Si local tiene cambios pendientes, NO lo pises
                        val localHasPending = !local.synced || local.pendingAction == "UPSERT" || local.pendingAction == "DELETE"
                        if (localHasPending) continue

                        val remoteIsNewer = remote.lastModified > local.lastModified
                        val different = isDifferent(local, cloudEntity)

                        if ((remoteIsNewer || different) && local.pendingAction != "DELETE") {
                            productDao.update(
                                cloudEntity.copy(
                                    id = local.id,
                                    synced = true,
                                    pendingAction = "", // tu DAO usa "" para limpiar
                                    lastModified = remote.lastModified
                                )
                            )
                            downloaded++
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    failed++
                }
            }

            SyncResult.Success(uploaded = 0, downloaded = downloaded, failed = failed)
        } catch (e: Exception) {
            e.printStackTrace()
            SyncResult.Error(e.message ?: "Error al descargar")
        }
    }

    private fun isDifferent(local: ProductEntity, cloud: ProductEntity): Boolean {
        return local.code != cloud.code ||
                local.description != cloud.description ||
                local.author != cloud.author ||
                local.category != cloud.category ||
                local.manufactureDate != cloud.manufactureDate ||
                local.cost != cloud.cost ||
                local.available != cloud.available ||
                local.photoUri != cloud.photoUri
    }
}

/**
 * Resultado de sincronización
 */
sealed class SyncResult {
    data class Success(val uploaded: Int, val downloaded: Int, val failed: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
    object NoInternet : SyncResult()
}
