// ec.edu.uce.book.data.entity.LastLoginEntity.kt
package ec.edu.uce.book.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_login")
data class LastLoginEntity(
    @PrimaryKey val id: Int = 1,          // siempre 1 (solo una fila)
    val currentTime: String,              // lo que te devuelve el servicio
    val savedAtMillis: Long = System.currentTimeMillis()
)
