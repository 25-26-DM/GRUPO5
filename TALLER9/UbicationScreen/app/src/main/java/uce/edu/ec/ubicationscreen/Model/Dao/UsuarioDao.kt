package uce.edu.ec.ubicationscreen.Model.Dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uce.edu.ec.ubicationscreen.Model.Entity.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insert(usuario: Usuario)

    @Query("""
        SELECT * FROM usuarios 
        WHERE nombreUsuario = :user AND password = :pass
    """)
    suspend fun login(user: String, pass: String): Usuario?
}
