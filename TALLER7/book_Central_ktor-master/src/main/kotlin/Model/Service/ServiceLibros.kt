package uce.edu.ec.Model.Service

import ec.edu.uce.Model.Repository.IreLibros
import uce.edu.ec.Model.Entity.Libros
import uce.edu.ec.Model.Entity.LibrosTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceLibros : IreLibros {

    override fun save(libros: Libros): Libros = transaction {
        val id = LibrosTable.insert {
            it[titulo] = libros.titulo
            it[autor] = libros.autor
            it[precio] = libros.precio
            it[descripcion] = libros.descripcion
            it[fechaPublicacion] = libros.fechaPublicacion
            it[imagen] = libros.imagen
        } get LibrosTable.id
        
        libros.copy(id = id)
    }

    override fun update(libros: Libros): Libros = transaction {
        val updatedRows = LibrosTable.update({ LibrosTable.id eq libros.id }) {
            it[titulo] = libros.titulo
            it[autor] = libros.autor
            it[precio] = libros.precio
            it[descripcion] = libros.descripcion
            it[fechaPublicacion] = libros.fechaPublicacion
            it[imagen] = libros.imagen
        }
        
        if (updatedRows > 0) libros else throw Exception("Libro no encontrado para actualizar")
    }

    override fun delete(libros: Libros) {
        transaction {
            LibrosTable.deleteWhere { LibrosTable.id eq libros.id }
        }
    }

    override fun obtenerLibroPorId(id: Int): Libros? = transaction {
        LibrosTable.selectAll().where { LibrosTable.id eq id }
            .map { rowToLibro(it) }
            .singleOrNull()
    }

    override fun obtenerTodos(): List<Libros> = transaction {
        LibrosTable.selectAll().map { rowToLibro(it) }
    }

    private fun rowToLibro(row: ResultRow): Libros {
        return Libros(
            id = row[LibrosTable.id],
            titulo = row[LibrosTable.titulo],
            autor = row[LibrosTable.autor],
            precio = row[LibrosTable.precio],
            descripcion = row[LibrosTable.descripcion],
            fechaPublicacion = row[LibrosTable.fechaPublicacion],
            imagen = row[LibrosTable.imagen]
        )
    }
}