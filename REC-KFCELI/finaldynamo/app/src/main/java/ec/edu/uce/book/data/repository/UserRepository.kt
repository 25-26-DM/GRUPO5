package ec.edu.uce.book.data.repository

import ec.edu.uce.book.data.dao.UserDao
import ec.edu.uce.book.data.entity.UserEntity
import ec.edu.uce.book.data.network.AuthApiService
import ec.edu.uce.book.data.network.TokenRequest
import ec.edu.uce.book.util.PasswordUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    private val authApiService = AuthApiService.create()

    // Enviar código aleatorio de 6 cifras por correo usando las credenciales proporcionadas
    suspend fun sendLoginCode(email: String, code: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = authApiService.sendToken(
                TokenRequest(
                    email = email,
                    token = code,
                    user = "grupo5ucedm@outlook.com",
                    pass = "ucedmgp5"
                )
            )
            response.success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Registrar usuario (Mantenido por compatibilidad)
    suspend fun registerUser(name: String, lastName: String, password: String): Boolean {
        val existingUser = userDao.getUserByName(name, lastName)
        if (existingUser != null) return false

        val hashedPassword = PasswordUtils.hash(password)
        val user = UserEntity(name = name, lastName = lastName, passwordHash = hashedPassword)
        userDao.insert(user)
        return true
    }

    // Login antiguo
    suspend fun login(name: String, lastName: String, password: String): Boolean {
        val user = userDao.getUserByName(name, lastName) ?: return false
        return PasswordUtils.verify(password, user.passwordHash)
    }
}