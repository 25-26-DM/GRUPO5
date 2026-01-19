package uce.edu.ec.Model.Entity

import kotlinx.serialization.Serializable

@Serializable
 data class Usuario(
     val id: Int,
     val nombreUsuario: String,
     val nombre: String,
     val apellido: String,
     val correo: String,
     val password: String,
     val rol: String,
     val metodoPagoFavorito: String
)