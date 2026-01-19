package uce.edu.ec.ubicationscreen.View.Componens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uce.edu.ec.ubicationscreen.Model.AppDatabase.AppDatabase
import uce.edu.ec.ubicationscreen.Model.Security.PasswordUtils
import uce.edu.ec.ubicationscreen.Model.Entity.Usuario


@Composable
fun RegisterUser(
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val usuarioDao = db.usuarioDao()

    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineLarge)

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") }
        )

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") }
        )

        if (error) {
            Text("Error al registrar usuario", color = MaterialTheme.colorScheme.error)
        }

        val scope = rememberCoroutineScope()

        Button(onClick = {
            error = false
            scope.launch {
                try {
                    usuarioDao.insert(
                        Usuario(
                            nombreUsuario = usuario,
                            password = PasswordUtils.hash(password),
                            nombre = nombre,
                            apellido = apellido
                        )
                    )
                    onRegisterSuccess()
                } catch (e: Exception) {
                    error = true
                }
            }
        }) {
            Text("Registrar")
        }


        TextButton(onClick = onBackClick) {
            Text("Volver")
        }
    }
}
