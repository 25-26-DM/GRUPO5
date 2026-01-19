package uce.edu.ec.Model.Entity

import kotlinx.serialization.Serializable

@Serializable
data class Libros(
    val id: Int,
    val titulo: String,
    val autor: String,
    val precio: Double,
    val descripcion: String,
    val fechaPublicacion: String,
    val imagen: String
)