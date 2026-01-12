package ec.edu.uce.taller6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uce.taller6.prefs.SessionPreferences

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val sessionPrefs = SessionPreferences(this)

        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()

            if (usuario.isNotBlank()) {
                sessionPrefs.saveLogin(usuario)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                etUsuario.error = "Ingrese el usuario"
            }
        }
    }
}
