package ec.edu.uce.book.controller

import ec.edu.uce.book.data.MemoryData
import ec.edu.uce.book.model.Product
import ec.edu.uce.book.model.User

class AuthController {

    fun register(name: String, lastName: String) {
        val user = User(name, lastName)
        MemoryData.users.add(user)
    }

    /**
     * Modificado para retornar el objeto User si el login es exitoso.
     */
    fun login(name: String, lastName: String): User? {
        val user = MemoryData.users.find {
            it.name == name && it.lastName == lastName
        }
        MemoryData.currentUser = user
        return user // Retorna el usuario o null
    }

    fun logout() {
        MemoryData.currentUser = null
    }

    fun getUsers(): List<User> = MemoryData.users

    // Nuevo método para obtener el usuario activo
    fun getCurrentUser(): User? = MemoryData.currentUser
}