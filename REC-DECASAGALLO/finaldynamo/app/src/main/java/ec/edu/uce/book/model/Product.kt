package ec.edu.uce.book.model

data class Product(
    val code: String,
    val description: String,
    val author: String,
    val category: String,
    val manufactureDate: String,
    val cost: Double,
    val available: Boolean
)