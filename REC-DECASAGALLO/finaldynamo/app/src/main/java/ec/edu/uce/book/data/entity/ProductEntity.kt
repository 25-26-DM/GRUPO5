package ec.edu.uce.book.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val description: String,
    val author: String,
    val category: String,
    val manufactureDate: String,
    val cost: Double,
    val available: Boolean,
    val photoUri: String? = null,


    // ====== CAMPOS PARA SINCRONIZACIÓN ======
    val lastModified: Long = System.currentTimeMillis(),
    val synced: Boolean = false,                // false = pendiente
    val pendingAction: String = "UPSERT"        // "UPSERT" o "DELETE"
)
