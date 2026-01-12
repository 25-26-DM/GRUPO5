package uce.edu.ec.Model.Service

import ec.edu.uce.Model.Repository.IreInventario
import uce.edu.ec.Model.Entity.Inventario
import uce.edu.ec.Model.Entity.Inventarios
import uce.edu.ec.Model.Entity.InventarioLibros
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ServiceInventario : IreInventario {

    override fun save(inventario: Inventario): Inventario = transaction {
        // Insertar el inventario principal
        val id = Inventarios.insert {
            // Si Inventarios tuviera más campos, irían aquí
        } get Inventarios.id

        // Insertar los libros asociados al inventario
        inventario.librosStock.forEach { (titulo, cantidad) ->
            InventarioLibros.insert {
                it[inventarioId] = id
                it[libroTitulo] = titulo
                it[this.cantidad] = cantidad
            }
        }
        
        inventario.copy(id = id)
    }

    override fun obtenerStock(tituloLibro: String): Int = transaction {
        // Sumar el stock de todas las entradas de inventario para este libro
        InventarioLibros.selectAll().where { InventarioLibros.libroTitulo eq tituloLibro }
            .sumOf { it[InventarioLibros.cantidad] }
    }

    override fun actualizarStock(tituloLibro: String, cantidad: Int) {
        transaction {
            // Esta lógica es simplificada. En un caso real, deberías saber qué inventario específico actualizar.
            // Aquí actualizamos la primera entrada que encontremos o insertamos si no existe, asumiendo un inventario global.
            
            // Intentamos encontrar si ya existe registro para este libro en algún inventario
            val existingEntry = InventarioLibros.selectAll().where { InventarioLibros.libroTitulo eq tituloLibro }.firstOrNull()
            
            if (existingEntry != null) {
                InventarioLibros.update({ InventarioLibros.libroTitulo eq tituloLibro }) {
                    it[this.cantidad] = cantidad
                }
            } else {
                // Si no existe, necesitamos un ID de inventario. 
                // Buscamos el primero o creamos uno por defecto.
                var inventarioId = Inventarios.selectAll().firstOrNull()?.get(Inventarios.id)
                
                if (inventarioId == null) {
                    inventarioId = Inventarios.insert { }.get(Inventarios.id)
                }
                
                InventarioLibros.insert {
                    it[this.inventarioId] = inventarioId!!
                    it[libroTitulo] = tituloLibro
                    it[this.cantidad] = cantidad
                }
            }
        }
    }
}