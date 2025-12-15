package ec.edu.uce.book

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import ec.edu.uce.book.model.Product
import ec.edu.uce.book.ui.theme.BookTheme
import ec.edu.uce.book.view.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BookTheme {
                Surface(color = MaterialTheme.colorScheme.background) {

                    var currentScreen by remember { mutableStateOf("welcome") }
                    var selectedIndex by remember { mutableStateOf<Int?>(null) }
                    var selectedProduct by remember { mutableStateOf<Product?>(null) }

                    when (currentScreen) {

                        "welcome" -> WelcomeScreen {
                            currentScreen = "login"
                        }

                        "login" -> LoginScreen(
                            onLoginSuccess = { currentScreen = "home" },
                            onRegister = { currentScreen = "register" }
                        )

                        "register" -> RegisterScreen(
                            onRegisterSuccess = { currentScreen = "login" },
                            onBackToLogin = { currentScreen = "login" }
                        )

                        "home" -> HomeScreen(
                            onLogout = { currentScreen = "login" },
                            onAddProduct = {
                                selectedIndex = null
                                selectedProduct = null
                                currentScreen = "form"
                            },
                            onEditProduct = { index, product ->
                                selectedIndex = index
                                selectedProduct = product
                                currentScreen = "form"
                            }
                        )

                        "form" -> ProductFormScreen(
                            productIndex = selectedIndex,
                            productToEdit = selectedProduct,
                            onFinish = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}
