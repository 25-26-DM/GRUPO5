package com.android.example.cameraxapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.example.cameraxapp.R
import com.android.example.cameraxapp.data.AppDatabase
import com.android.example.cameraxapp.data.UserEntity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = AppDatabase.getInstance(this)

        val etUser = findViewById<TextInputEditText>(R.id.etUser)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val u = etUser.text?.toString()?.trim().orEmpty()
            val p = etPass.text?.toString()?.trim().orEmpty()

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Complete los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val exists = db.userDao().findByUsername(u)
                if (exists != null) {
                    Toast.makeText(this@RegisterActivity, "Ese usuario ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    db.userDao().insert(UserEntity(username = u, password = p))
                    Toast.makeText(this@RegisterActivity, "Registrado ", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
