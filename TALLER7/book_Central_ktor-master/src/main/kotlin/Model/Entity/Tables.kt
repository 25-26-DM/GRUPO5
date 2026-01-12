package uce.edu.ec.Model.Entity

import org.jetbrains.exposed.sql.Table

object Usuarios : Table() {
    val id = integer("id").autoIncrement()
    val nombreUsuario = varchar("nombre_usuario", 50)
    val nombre = varchar("nombre", 50)
    val apellido = varchar("apellido", 50)
    val correo = varchar("correo", 50)
    val password = varchar("password", 50)
    val rol = varchar("rol", 50)
    val metodoPagoFavorito = varchar("metodo_pago_favorito", 50)

    override val primaryKey = PrimaryKey(id)
}

object LibrosTable : Table("libros") {
    val id = integer("id").autoIncrement()
    val titulo = varchar("titulo", 50)
    val autor = varchar("autor", 50)
    val precio = double("precio")
    val descripcion = text("descripcion")
    val fechaPublicacion = varchar("fecha_publicacion", 50)
    val imagen = varchar("imagen", 50)

    override val primaryKey = PrimaryKey(id)
}

object Inventarios : Table() {
    val id = integer("id").autoIncrement()
    // Para simplificar, en una base de datos relacional, el stock suele estar relacionado con el libro.
    // Sin embargo, basándonos en tu entidad Inventario que tiene un mapa, podríamos necesitar una tabla intermedia
    // o repensar la estructura. Por ahora, crearemos una tabla simple de inventario que podría relacionarse con libros.
    
    override val primaryKey = PrimaryKey(id)
}

// Tabla intermedia para manejar la relación librosStock del Inventario
object InventarioLibros : Table("inventario_libros") {
    val inventarioId = reference("inventario_id", Inventarios.id)
    val libroTitulo = varchar("libro_titulo", 50) // O referencia a LibrosTable.id si prefieres
    val cantidad = integer("cantidad")
    
    override val primaryKey = PrimaryKey(inventarioId, libroTitulo)
}