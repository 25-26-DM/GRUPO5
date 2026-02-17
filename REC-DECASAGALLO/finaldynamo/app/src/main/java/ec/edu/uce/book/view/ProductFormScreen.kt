package ec.edu.uce.book.view

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ec.edu.uce.book.data.entity.ProductEntity
import ec.edu.uce.book.viewmodel.ProductViewModel
import ec.edu.uce.book.util.CameraUtils
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    productToEdit: ProductEntity? = null,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // Focus Requesters para navegación entre campos
    val codeFocus = remember { FocusRequester() }
    val descriptionFocus = remember { FocusRequester() }
    val authorFocus = remember { FocusRequester() }
    val costFocus = remember { FocusRequester() }

    var code by remember { mutableStateOf(productToEdit?.code ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var author by remember { mutableStateOf(productToEdit?.author ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "") }
    var date by remember { mutableStateOf(productToEdit?.manufactureDate ?: "") }
    var cost by remember { mutableStateOf(productToEdit?.cost?.toString() ?: "") }
    var available by remember { mutableStateOf(productToEdit?.available ?: true) }
    var photoUri by remember { mutableStateOf<Uri?>(productToEdit?.photoUri?.let { Uri.parse(it) }) }

    var showSuccess by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Variable para forzar recomposición cuando se toma foto
    var photoUpdateTrigger by remember { mutableStateOf(0) }

    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // Forzar actualización de la UI
            photoUpdateTrigger++
        }
    }

    // Lista de categorías
    val categories = listOf(
        "Novela",
        "Ciencia Ficción",
        "Fantasía",
        "Historia",
        "Biografía",
        "Ciencia",
        "Tecnología",
        "Arte",
        "Filosofía",
        "Poesía",
        "Drama",
        "Terror",
        "Misterio",
        "Romance",
        "Autoayuda",
        "Infantil",
        "Juvenil",
        "Cómic",
        "Ensayo",
        "Otro"
    )

    val hasChanges by remember(code, description, author, category, date, cost, available, photoUri, photoUpdateTrigger) {
        mutableStateOf(
            productToEdit == null ||
                    code != productToEdit.code ||
                    description != productToEdit.description ||
                    author != productToEdit.author ||
                    category != productToEdit.category ||
                    date != productToEdit.manufactureDate ||
                    cost != productToEdit.cost.toString() ||
                    available != productToEdit.available ||
                    photoUri?.toString() != productToEdit.photoUri
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar con gradiente
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF667eea),
                                    Color(0xFF764ba2)
                                )
                            )
                        )
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                if (productToEdit == null) "Agregar Libro" else "Editar Libro",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onFinish) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // SECCIÓN DE FOTO - Key con photoUpdateTrigger para forzar recomposición
                key(photoUpdateTrigger) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .shadow(8.dp, shape = RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (photoUri != null) Color.White else Color(0xFFF5F5F5)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUri != null) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "Foto del producto",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "📷",
                                        fontSize = 64.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Sin foto",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF9E9E9E)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN PARA TOMAR FOTO
                Button(
                    onClick = {
                        if (cameraPermission.status.isGranted) {
                            val uri = CameraUtils.createImageUri(context)
                            photoUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF667eea)
                    )
                ) {
                    Text(
                        text = if (photoUri != null) "📷 Cambiar Foto" else "📷 Tomar Foto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // CARD DEL FORMULARIO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = "Información del Libro",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        // Campo Código
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Código") },
                            placeholder = { Text("Ej: B001") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(codeFocus),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { descriptionFocus.requestFocus() }
                            )
                        )

                        // Campo Descripción
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Título del Libro") },
                            placeholder = { Text("Ej: Don Quijote de la Mancha") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(descriptionFocus),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { authorFocus.requestFocus() }
                            )
                        )

                        // Campo Autor
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Autor") },
                            placeholder = { Text("Ej: Miguel de Cervantes") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(authorFocus),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.clearFocus()
                                    showCategoryMenu = true
                                }
                            )
                        )

                        // Campo Categoría con Dropdown
                        ExposedDropdownMenuBox(
                            expanded = showCategoryMenu,
                            onExpandedChange = { showCategoryMenu = it }
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Categoría") },
                                placeholder = { Text("Selecciona una categoría") },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Expandir"
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF667eea),
                                    focusedLabelColor = Color(0xFF667eea)
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = showCategoryMenu,
                                onDismissRequest = { showCategoryMenu = false }
                            ) {
                                categories.forEach { categoryOption ->
                                    DropdownMenuItem(
                                        text = { Text(categoryOption) },
                                        onClick = {
                                            category = categoryOption
                                            showCategoryMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Campo Fecha con DatePicker
                        OutlinedTextField(
                            value = date,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Fecha de Publicación") },
                            placeholder = { Text("dd/MM/yyyy") },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val calendar = Calendar.getInstance()

                                        // Si ya hay una fecha, usarla como inicial
                                        if (date.isNotEmpty()) {
                                            try {
                                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                val parsedDate = dateFormat.parse(date)
                                                if (parsedDate != null) {
                                                    calendar.time = parsedDate
                                                }
                                            } catch (e: Exception) {
                                                // Si falla, usar fecha actual
                                            }
                                        }

                                        val year = calendar.get(Calendar.YEAR)
                                        val month = calendar.get(Calendar.MONTH)
                                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                                        DatePickerDialog(
                                            context,
                                            { _, selectedYear, selectedMonth, selectedDay ->
                                                val selectedDate = Calendar.getInstance()
                                                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                                                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                                date = dateFormat.format(selectedDate.time)
                                            },
                                            year,
                                            month,
                                            day
                                        ).show()
                                    }
                                ) {
                                    Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            ),
                            singleLine = true
                        )

                        // Campo Costo (solo números)
                        OutlinedTextField(
                            value = cost,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                    cost = newValue
                                }
                            },
                            label = { Text("Precio (USD)") },
                            placeholder = { Text("Ej: 25.50") },
                            leadingIcon = {
                                Text(
                                    "$",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF667eea)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(costFocus),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF667eea),
                                focusedLabelColor = Color(0xFF667eea)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Switch Disponibilidad
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F7FA)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Disponibilidad",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        if (available) "El libro está disponible" else "El libro no está disponible",
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                                Switch(
                                    checked = available,
                                    onCheckedChange = { available = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF667eea),
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFBDBDBD)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje de éxito
                AnimatedVisibility(
                    visible = showSuccess,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (productToEdit == null) "Libro agregado exitosamente" else "Libro actualizado exitosamente",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // BOTÓN GUARDAR
                Button(
                    onClick = {
                        scope.launch {
                            val product = ProductEntity(
                                id = productToEdit?.id ?: 0,
                                code = code,
                                description = description,
                                author = author,
                                category = category,
                                manufactureDate = date,
                                cost = cost.toDoubleOrNull() ?: 0.0,
                                available = available,
                                photoUri = photoUri?.toString()
                            )
                            if (productToEdit == null) productViewModel.addProduct(product)
                            else productViewModel.updateProduct(product)

                            showSuccess = true
                            kotlinx.coroutines.delay(1500)
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = hasChanges &&
                            code.isNotBlank() &&
                            description.isNotBlank() &&
                            author.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF667eea),
                        disabledContainerColor = Color(0xFFBDBDBD)
                    )
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (productToEdit == null) "Agregar Libro" else "Guardar Cambios",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}