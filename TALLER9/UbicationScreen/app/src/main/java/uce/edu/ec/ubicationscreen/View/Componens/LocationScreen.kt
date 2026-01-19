package uce.edu.ec.ubicationscreen.View.Componens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import com.google.accompanist.permissions.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    @SuppressLint("MissingPermission")
    fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    error = null
                } ?: run {
                    error = "No se pudo obtener la ubicación"
                }
            }
    }

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        } else {
            getLocation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación Actual") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when {
                latitude != null && longitude != null -> {
                    Text("Latitud: $latitude")
                    Text("Longitud: $longitude")
                }

                locationPermission.status.isGranted.not() -> {
                    Text(
                        "Permiso de ubicación requerido",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                error != null -> {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (locationPermission.status.isGranted) {
                        getLocation()
                    } else {
                        locationPermission.launchPermissionRequest()
                    }
                }
            ) {
                Text("Actualizar ubicación")
            }
        }
    }
}
