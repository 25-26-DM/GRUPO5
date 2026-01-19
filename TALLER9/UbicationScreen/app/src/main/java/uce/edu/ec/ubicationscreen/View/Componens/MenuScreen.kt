package uce.edu.ec.ubicationscreen.View.Componens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onSensorsClick: () -> Unit,
    onLocationClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onSensorsClick) {
            Text("Ver Sensores")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onLocationClick) {
            Text("Ver Ubicación")
        }
    }
}
