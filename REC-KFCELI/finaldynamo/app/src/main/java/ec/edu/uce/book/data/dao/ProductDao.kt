package ec.edu.uce.book.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import ec.edu.uce.book.data.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insert(product: ProductEntity): Long

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>


    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    // ===== PENDIENTES SYNC =====
    @Query("SELECT * FROM products WHERE synced = 0")
    suspend fun getPendingProducts(): List<ProductEntity>

    @Query("UPDATE products SET synced = 1, pendingAction = '' WHERE id = :id")
    suspend fun markAsSynced(id: Int)

    @Query("UPDATE products SET synced = 0, pendingAction = :action, lastModified = :ts WHERE id = :id")
    suspend fun markAsPending(id: Int, action: String, ts: Long)

    @Query("DELETE FROM products WHERE pendingAction = 'DELETE' AND synced = 1")
    suspend fun deleteSyncedDeletes()

    @Query("SELECT * FROM products WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): ProductEntity?

    @Query("UPDATE products SET synced = 1, pendingAction = '' WHERE code = :code")
    suspend fun markAsSyncedByCode(code: String)


}
