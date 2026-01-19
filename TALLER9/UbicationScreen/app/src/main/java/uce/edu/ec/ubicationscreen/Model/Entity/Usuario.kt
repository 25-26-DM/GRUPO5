package uce.edu.ec.ubicationscreen.Model.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreUsuario: String,
    val password: String,
    val nombre: String,
    val apellido: String
)
