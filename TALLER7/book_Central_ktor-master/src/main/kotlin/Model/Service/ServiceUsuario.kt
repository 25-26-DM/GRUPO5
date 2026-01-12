package uce.edu.ec.Model.Service

import ec.edu.uce.Model.Repository.IreUsuario
import uce.edu.ec.Model.Entity.Usuario
import uce.edu.ec.Model.Entity.Usuarios
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceUsuario : IreUsuario {

    override fun save(usuario: Usuario): Usuario = transaction {
        val id = Usuarios.insert {
            it[nombreUsuario] = usuario.nombreUsuario
            it[nombre] = usuario.nombre
            it[apellido] = usuario.apellido
            it[correo] = usuario.correo
            it[password] = usuario.password
            it[rol] = usuario.rol
            it[metodoPagoFavorito] = usuario.metodoPagoFavorito
        } get Usuarios.id
        
        usuario.copy(id = id)
    }

    override fun update(usuario: Usuario): Usuario = transaction {
        val updatedRows = Usuarios.update({ Usuarios.id eq usuario.id }) {
            it[nombreUsuario] = usuario.nombreUsuario
            it[nombre] = usuario.nombre
            it[apellido] = usuario.apellido
            it[correo] = usuario.correo
            it[password] = usuario.password
            it[rol] = usuario.rol
            it[metodoPagoFavorito] = usuario.metodoPagoFavorito
        }
        
        if (updatedRows > 0) usuario else throw Exception("Usuario no encontrado para actualizar")
    }

    override fun delete(usuario: Usuario) {
        transaction {
            Usuarios.deleteWhere { Usuarios.id eq usuario.id }
        }
    }

    override fun obtenerUsuarioPorId(id: Int): Usuario? = transaction {
        Usuarios.selectAll().where { Usuarios.id eq id }
            .map { rowToUsuario(it) }
            .singleOrNull()
    }

    override fun obtenerUsuarioPorNombreUsuario(nombreUsuario: String): Usuario? = transaction {
        Usuarios.selectAll().where { Usuarios.nombreUsuario eq nombreUsuario }
            .map { rowToUsuario(it) }
            .singleOrNull()
    }

    override fun obtenerUsuarioPorNombreYApellido(nombre: String, apellido: String): Usuario? = transaction {
        Usuarios.selectAll().where { (Usuarios.nombre.lowerCase() eq nombre.lowercase()) and (Usuarios.apellido.lowerCase() eq apellido.lowercase()) }
            .map { rowToUsuario(it) }
            .singleOrNull()
    }

    private fun rowToUsuario(row: ResultRow): Usuario {
        return Usuario(
            id = row[Usuarios.id],
            nombreUsuario = row[Usuarios.nombreUsuario],
            nombre = row[Usuarios.nombre],
            apellido = row[Usuarios.apellido],
            correo = row[Usuarios.correo],
            password = row[Usuarios.password],
            rol = row[Usuarios.rol],
            metodoPagoFavorito = row[Usuarios.metodoPagoFavorito]
        )
    }
}