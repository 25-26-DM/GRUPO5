package ec.edu.uce.taller2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.uce.taller2.ui.theme.TALLER2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TALLER2Theme  {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    InvitationCard(
                        onShareClicked = {
                            compartirInvitacion(this)
                        }
                    )

                }
            }
        }
    }
}


fun compartirInvitacion(context: Context) {
    val mensaje = """
        Invitación Especial 

        Fecha: Sábado, 15 de Noviembre
        Lugar: Quito, Parque La Carolina
        Correo: evento@gmail.com
        Teléfono: +593 987 654 321
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, mensaje)
    }

    context.startActivity(Intent.createChooser(intent, "Compartir invitación"))
}

@Preview(showBackground = true)
@Composable
fun PreviewInvitationCard() {
    TALLER2Theme {
        InvitationCard()
    }
}@Composable
fun InvitationCard(onShareClicked: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.event),
                contentDescription = "Imagen del evento",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            // Título
            Text(
                text = "FESTIVAL DE MUSICA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {

                // Fecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                    Text(
                        "Sábado, 15 de Noviembre",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Lugar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Text(
                        "Quito, Parque La Carolina",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Correo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Email, contentDescription = null)
                    Text(
                        "evento@gmail.com",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Teléfono
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = null)
                    Text(
                        "+593 987 654 321",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF6A1B9A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Compartir invitación",
                        fontSize = 18.sp
                    )
                }



                Spacer(Modifier.height(16.dp))

                // Íconos redes sociales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { /* Facebook */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = "Facebook",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                    }

                    IconButton(onClick = { /* WhatsApp */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.whatsapp),
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                    }

                    IconButton(onClick = { /* Instagram */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.instagram),
                            contentDescription = "Instagram",
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}
