package ec.edu.uce.book.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ec.edu.uce.book.data.dao.LastLoginDao
import ec.edu.uce.book.data.dao.UserDao
import ec.edu.uce.book.data.dao.ProductDao
import ec.edu.uce.book.data.entity.UserEntity
import ec.edu.uce.book.data.entity.ProductEntity

@Database(
    entities = [UserEntity::class, ProductEntity::class],
    version = 3, //// ← CAMBIO: Era 2, ahora es 3
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    abstract fun lastLoginDao(): LastLoginDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bookstore_db"
                )
                    .fallbackToDestructiveMigration()  // ← Esto borrará los datos al cambiar versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}