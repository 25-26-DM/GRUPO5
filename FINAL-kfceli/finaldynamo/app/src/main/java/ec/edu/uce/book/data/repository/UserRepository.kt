package ec.edu.uce.book.data.repository

import ec.edu.uce.book.data.dao.UserDao
import ec.edu.uce.book.data.entity.UserEntity
import ec.edu.uce.book.util.PasswordUtils

class UserRepository(private val userDao: UserDao) {

    // Registrar usuario
    suspend fun registerUser(name: String, lastName: String, password: String): Boolean {
        val existingUser = userDao.getUserByName(name, lastName)
        if (existingUser != null) return false

        val hashedPassword = PasswordUtils.hash(password)
        val user = UserEntity(name = name, lastName = lastName, passwordHash = hashedPassword)
        userDao.insert(user)
        return true
    }

    // Login
    suspend fun login(name: String, lastName: String, password: String): Boolean {
        val user = userDao.getUserByName(name, lastName) ?: return false
        return PasswordUtils.verify(password, user.passwordHash)
    }
}