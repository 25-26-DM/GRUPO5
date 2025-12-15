package ec.edu.uce.book.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.uce.book.R
import ec.edu.uce.book.controller.AuthController
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit
) {
    val authController = remember { AuthController() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... (Contenido visual omitido por brevedad)

        Image(
            painter = painterResource(id = R.drawable.logo_book),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text("BookStore UCE", fontSize = 26.sp)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val user = authController.login(name, lastName)
                if (user != null) {

                    // 1. La "contraseña" es el apellido (lastName)
                    val password = user.lastName

                    // 2. Calcular "Hash" simulado
                    // El hash ahora se basa *solo* en la contraseña/apellido.
                    val simulatedHash = password.hashCode().toString().take(8)

                    // 3. Mostrar Toast SÓLO con Contraseña (Apellido) y Hash
                    showLoginToast(context, password, simulatedHash) // Llamada modificada

                    // 4. Navegar a la pantalla principal
                    onLoginSuccess()
                } else {
                    error = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        TextButton(onClick = onRegister) {
            Text("Registrarse")
        }

        if (error) {
            Text("Usuario no registrado", color = Color.Red)
        }
    }
}

private fun showLoginToast(context: Context, password: String, hash: String) {
    val message = "Contraseña: $password\nHash: $hash"
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}