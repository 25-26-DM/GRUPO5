package uce.edu.ec.ubicationscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import uce.edu.ec.ubicationscreen.View.Componens.*
import uce.edu.ec.ubicationscreen.theme.BookStoreTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BookStoreTheme {

                var currentScreen = remember { mutableStateOf("login") }

                when (currentScreen.value) {
                    "login" -> LoginUser(
                        onLoginSuccess = { currentScreen.value = "menu" },
                        onRegisterClick = { currentScreen.value = "register" }
                    )

                    "menu" -> MenuScreen(
                        onSensorsClick = { currentScreen.value = "sensorList" },
                        onLocationClick = { currentScreen.value = "location" }
                    )

                    "sensorList" -> SensorListScreen(
                        onBackClick = { currentScreen.value = "menu" }
                    )

                    "location" -> LocationScreen(
                        onBackClick = { currentScreen.value = "menu" }
                    )

                    "register" -> RegisterUser(
                        onRegisterSuccess = { currentScreen.value = "login" },
                        onBackClick = { currentScreen.value = "login" }
                    )
                }
            }
        }
    }
}
