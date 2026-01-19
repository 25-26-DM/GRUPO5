package uce.edu.ec.ubicationscreen.View.Componens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorListScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val deviceSensors = remember {
        sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sensores del dispositivo") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(deviceSensors) { sensor ->
                SensorItem(sensor)
            }
        }
    }
}

@Composable
fun SensorItem(sensor: Sensor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = sensor.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Tipo: ${sensor.type}")
            Text(text = "Fabricante: ${sensor.vendor}")
        }
    }
}

