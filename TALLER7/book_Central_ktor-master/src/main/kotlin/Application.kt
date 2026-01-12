package ec.edu.uce

import ec.edu.uce.Configure.configureDatabases
import ec.edu.uce.Restcontroler.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
}
