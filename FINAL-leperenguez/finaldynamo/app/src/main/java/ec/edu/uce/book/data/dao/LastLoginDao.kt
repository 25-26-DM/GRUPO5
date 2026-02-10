// ec.edu.uce.book.data.dao.LastLoginDao.kt
package ec.edu.uce.book.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ec.edu.uce.book.data.entity.LastLoginEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastLoginDao {

    @Query("SELECT * FROM last_login WHERE id = 1 LIMIT 1")
    fun observeLastLogin(): Flow<LastLoginEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: LastLoginEntity)
}
