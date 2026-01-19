package ec.edu.uce.Configure


import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import uce.edu.ec.Model.Entity.Usuarios
import uce.edu.ec.Model.Entity.LibrosTable
import uce.edu.ec.Model.Entity.Inventarios
import uce.edu.ec.Model.Entity.InventarioLibros

fun Application.configureDatabases() {
    val database = Database.connect(
        url = environment.config.property("postgres.url").getString(),
        user = environment.config.property("postgres.user").getString(),
        password = environment.config.property("postgres.password").getString(),
        driver = "org.postgresql.Driver"
    )
    
    transaction(database) {
        SchemaUtils.create(Usuarios, LibrosTable, Inventarios, InventarioLibros)
    }
}
