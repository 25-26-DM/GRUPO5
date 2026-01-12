package ec.edu.uce.Restcontroler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import uce.edu.ec.Model.Entity.Inventario
import uce.edu.ec.Model.Entity.Libros
import uce.edu.ec.Model.Service.ServiceUsuario
import uce.edu.ec.Model.Entity.Usuario
import uce.edu.ec.Model.Service.ServiceInventario
import uce.edu.ec.Model.Service.ServiceLibros

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    
    val serviceUsuario = ServiceUsuario()
    val serviceLibros = ServiceLibros()
    val serviceInventario = ServiceInventario()

    routing {
        get("/book-central") {
            call.respondText("Hello World!")
        }

        route("/usuarios") {
            // Crear usuario
            post {
                val usuario = call.receive<Usuario>()
                val nuevoUsuario = serviceUsuario.save(usuario)
                call.respond(HttpStatusCode.Created, nuevoUsuario)
            }

            // Obtener usuario por ID
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@get
                }
                val usuario = serviceUsuario.obtenerUsuarioPorId(id)
                if (usuario != null) {
                    call.respond(usuario)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                }
            }

            // Actualizar usuario
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@put
                }
                val usuario = call.receive<Usuario>()
                // Aseguramos que el ID del objeto coincida con el de la URL
                val usuarioActualizado = serviceUsuario.update(usuario.copy(id = id))
                call.respond(usuarioActualizado)
            }

            // Eliminar usuario
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@delete
                }
                // Necesitamos un objeto usuario con el ID para borrarlo, o modificar el servicio para borrar por ID.
                // Dado que el servicio pide un objeto Usuario, buscamos primero.
                val usuario = serviceUsuario.obtenerUsuarioPorId(id)
                if (usuario != null) {
                    serviceUsuario.delete(usuario)
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Usuario no encontrado")
                }
            }
        }
    }
    routing {
        route("/libros"){
            // crear libro
            post {
                val libro = call.receive<Libros>()
                val nuevoLibro = serviceLibros.save(libro)
                call.respond(HttpStatusCode.Created, nuevoLibro)
            }
        }
    }
    routing {
        route("/inventario"){
            // crear inventario
            post {
                val inventario = call.receive<Inventario>()
                val nuevoInventario = serviceInventario.save(inventario)
                call.respond(HttpStatusCode.Created, nuevoInventario)
            }
        }
    }
}