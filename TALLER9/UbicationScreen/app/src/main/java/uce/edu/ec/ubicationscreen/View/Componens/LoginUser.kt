package uce.edu.ec.ubicationscreen.View.Componens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uce.edu.ec.ubicationscreen.Model.AppDatabase.AppDatabase
import uce.edu.ec.ubicationscreen.Model.Security.PasswordUtils
import uce.edu.ec.ubicationscreen.R

@Composable
fun LoginUser(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val usuarioDao = db.usuarioDao()
    val scope = rememberCoroutineScope()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {

        Image(
            painter = painterResource(id = R.drawable.logo_grupo),
            contentDescription = "Logo del grupo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (error) {
            Text("Credenciales incorrectas", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            scope.launch {
                val hash = PasswordUtils.hash(password)
                val userExist = usuarioDao.login(usuario, hash)

                if (userExist != null) {
                    onLoginSuccess()
                } else {
                    error = true
                }
            }
        }) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
