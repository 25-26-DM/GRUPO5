package ec.edu.uce.book.controller

import ec.edu.uce.book.data.MemoryData
import ec.edu.uce.book.model.User

class AuthController {

    fun register(name: String, lastName: String) {
        val user = User(name, lastName)
        MemoryData.users.add(user)
    }

    fun login(name: String, lastName: String): Boolean {
        val user = MemoryData.users.find {
            it.name == name && it.lastName == lastName
        }
        MemoryData.currentUser = user
        return user != null
    }

    fun logout() {
        MemoryData.currentUser = null
    }
}
