package ec.edu.uce.book.view

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ec.edu.uce.book.util.LoginTokenRecService
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String) -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }

    var tokenRequested by remember { mutableStateOf(false) }
    var requestingToken by remember { mutableStateOf(false) }
    var verifyingToken by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else null

    fun requestToken() {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) {
            showError = true
            showInfo = false
            message = "Ingresa el correo del grupo"
            return
        }

        requestingToken = true
        showError = false
        showInfo = false
        message = ""

        scope.launch {
            val result = LoginTokenRecService.requestToken(cleanEmail)
            requestingToken = false

            result.fold(
                onSuccess = { msg ->
                    tokenRequested = true
                    showInfo = true
                    showError = false
                    message = msg.ifBlank { "Código enviado al correo" }
                },
                onFailure = { e ->
                    showError = true
                    showInfo = false
                    message = "Error enviando código: ${e.message}"
                }
            )
        }
    }

    fun verifyAndLogin() {
        val cleanEmail = email.trim()
        val cleanToken = token.trim()

        if (cleanEmail.isBlank()) {
            showError = true
            showInfo = false
            message = "Ingresa el correo del grupo"
            return
        }
        if (cleanToken.length != 6) {
            showError = true
            showInfo = false
            message = "El código debe tener 6 dígitos"
            return
        }

        verifyingToken = true
        showError = false
        showInfo = false
        message = ""

        scope.launch {
            val result = LoginTokenRecService.verifyToken(cleanEmail, cleanToken)
            verifyingToken = false

            result.fold(
                onSuccess = { ok ->
                    if (ok) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (notificationPermission?.status?.isGranted == false) {
                                notificationPermission.launchPermissionRequest()
                            }
                        }
                        onLoginSuccess(cleanEmail)
                    } else {
                        showError = true
                        showInfo = false
                        message = "Código incorrecto"
                    }
                },
                onFailure = { e ->
                    showError = true
                    showInfo = false
                    message = "Error validando código: ${e.message}"
                }
            )
        }
    }

    val anyLoading = requestingToken || verifyingToken

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF667eea), Color(0xFF764ba2))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login por correo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ingresa el correo del grupo y recibe un código de 6 dígitos",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            showError = false
                            showInfo = false
                            message = ""

                            // si cambian el correo, reiniciamos el flujo del token
                            tokenRequested = false
                            token = ""
                        },
                        label = { Text("Correo del grupo") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !anyLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { requestToken() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !anyLoading && email.trim().isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea))
                    ) {
                        if (requestingToken) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(if (tokenRequested) "Reenviar código" else "Enviar código")
                    }

                    AnimatedVisibility(
                        visible = tokenRequested,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(18.dp))

                            OutlinedTextField(
                                value = token,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() }.take(6)
                                    token = filtered
                                    showError = false
                                    showInfo = false
                                    message = ""
                                },
                                label = { Text("Código (6 dígitos)") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Token") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                enabled = !anyLoading,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { verifyAndLogin() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                enabled = !anyLoading && token.trim().length == 6,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea))
                            ) {
                                if (verifyingToken) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text("Ingresar")
                            }
                        }
                    }

                    AnimatedVisibility(visible = showInfo, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = "✅ $message",
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    AnimatedVisibility(visible = showError, enter = fadeIn(), exit = fadeOut()) {
                        Text(
                            text = "❌ $message",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !anyLoading,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF667eea))
                    ) {
                        Text("Registrar (si aplica)")
                    }
                }
            }
        }
    }
}
