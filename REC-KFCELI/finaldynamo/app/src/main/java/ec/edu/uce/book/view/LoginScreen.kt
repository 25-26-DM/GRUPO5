package ec.edu.uce.book.view

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import ec.edu.uce.book.viewmodel.UserViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit
) {
    var email by remember { mutableStateOf("grupo5ucedm@outlook.com") }
    var code by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }

    val codeSentResult by userViewModel.codeSentResult.collectAsState()
    val verificationResult by userViewModel.verificationResult.collectAsState()
    
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else null

    LaunchedEffect(codeSentResult) {
        if (codeSentResult == true) {
            isCodeSent = true
            showError = false
            userViewModel.clearCodeSentResult()
        } else if (codeSentResult == false) {
            showError = true
            errorMessage = "Error al enviar el código. Verifique el correo."
            userViewModel.clearCodeSentResult()
        }
    }

    LaunchedEffect(verificationResult) {
        if (verificationResult == true) {
            userViewModel.clearVerificationResult()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (notificationPermission?.status?.isGranted == false) {
                    notificationPermission.launchPermissionRequest()
                }
            }
            onLoginSuccess()
        } else if (verificationResult == false) {
            showError = true
            errorMessage = "Código incorrecto. Intente de nuevo."
            userViewModel.clearVerificationResult()
        }
    }

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
                text = "¡Bienvenido!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!isCodeSent) "Ingrese su correo institucional" else "Ingrese el código de 6 dígitos",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(40.dp))

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

                    if (!isCodeSent) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { 
                                if (it.length <= 6) code = it 
                                showError = false
                            },
                            label = { Text("Código de 6 dígitos") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Código") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "❌ $errorMessage",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (!isCodeSent) {
                                userViewModel.sendLoginCode(email)
                            } else {
                                userViewModel.verifyLoginCode(code)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                        enabled = if (!isCodeSent) email.isNotBlank() else code.length == 6
                    ) {
                        Text(
                            text = if (!isCodeSent) "Enviar Código" else "Verificar e Ingresar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (isCodeSent) {
                        TextButton(onClick = { isCodeSent = false; code = "" }) {
                            Text("Cambiar correo", color = Color(0xFF667eea))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF667eea))
                    ) {
                        Text(text = "¿No tienes cuenta? Regístrate", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
