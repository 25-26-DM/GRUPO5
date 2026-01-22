package com.android.example.cameraxapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "taller10_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)


                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                val dao = database.userDao()


                                if (dao.countUsers() == 0) {
                                    val preUsers = listOf(
                                        UserEntity(username = "kevin",  password = "celi"),
                                        UserEntity(username = "jhonny", password = "ninabanda"),
                                        UserEntity(username = "luis",   password = "perenguez"),
                                        UserEntity(username = "dylan",  password = "lema"),
                                        UserEntity(username = "diego",  password = "casagallo")
                                    )

                                    preUsers.forEach { dao.insert(it) }
                                }
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
