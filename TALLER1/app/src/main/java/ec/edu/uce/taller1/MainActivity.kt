package ec.edu.uce.taller1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.uce.taller1.ui.theme.TALLER1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TALLER1Theme {
                GreetingCardContent()
            }
        }
    }
}

@Composable
fun GreetingCardContent() {

    // Fondo degradado con colores UCE
    val uceBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFB71C1C),  // Rojo UCE
            Color(0xFF0022FF)   // Amarillo UCE
        )
    )

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .background(uceBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Dispositivos Móviles",
                fontSize = 26.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Grupo: 5",
                fontSize = 22.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Integrantes:\n• CASAGALLO CARLOSAMA DIEGO EDUARDO\n• CELI DIAZ KEVIN FRANCISCO \n• LEMA CASA DYLAN ANTONIO\n• NINABANDA PAMBABAY JHONNY EDUARDO\n• PERENGUEZ BASTIDAS LUIS ESTEBAN",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TALLER1Theme {
        GreetingCardContent()
    }
}
