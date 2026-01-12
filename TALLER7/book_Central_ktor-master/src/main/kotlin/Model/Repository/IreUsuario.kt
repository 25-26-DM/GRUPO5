package ec.edu.uce.Model.Repository

import uce.edu.ec.Model.Entity.Usuario

interface IreUsuario {
    fun save(usuario: Usuario): Usuario
    fun update(usuario: Usuario): Usuario
    fun delete(usuario: Usuario)
    fun obtenerUsuarioPorId(id: Int): Usuario?
    fun obtenerUsuarioPorNombreUsuario(nombreUsuario: String): Usuario?
    fun obtenerUsuarioPorNombreYApellido(nombre: String, apellido: String): Usuario?
}