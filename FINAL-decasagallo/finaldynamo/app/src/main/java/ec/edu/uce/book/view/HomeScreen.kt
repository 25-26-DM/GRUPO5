package ec.edu.uce.book.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ec.edu.uce.book.data.entity.ProductEntity
import ec.edu.uce.book.viewmodel.ProductViewModel
import ec.edu.uce.book.util.SyncService
import ec.edu.uce.book.util.SyncResult
import ec.edu.uce.book.util.NotificationHelper
import kotlinx.coroutines.launch
import ec.edu.uce.book.util.MsgEditService


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    isOnline: Boolean,
    onLogout: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (ProductEntity) -> Unit
)
{
    val products by productViewModel.products.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(isOnline) {
        if (isOnline) {
            productViewModel.syncNow()
        }
    }

    val appDb = remember { ec.edu.uce.book.data.database.AppDatabase.getDatabase(context) }
    val productDao = remember { appDb.productDao() }

// OJO: aquí necesitas tu DynamoDBHelper
    val dynamoHelper = remember { ec.edu.uce.book.data.remote.DynamoDBHelper(context) }

    val syncService = remember {
        ec.edu.uce.book.util.SyncService(
            context = context,
            productRepository = productViewModel.repository, // ← si NO existe, no pasa nada, te digo abajo
            productDao = productDao,
            dynamoDBHelper = dynamoHelper
        )
    }


    var showEditBlockedDialog by remember { mutableStateOf(false) }
    var editBlockedMessage by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var isSyncing by remember { mutableStateOf(false) }
    var syncMessage by remember { mutableStateOf("") }
    var showSyncDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "BookStore UCE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) Color(0xFF4CAF50) else Color(0xFFF44336))
                            )
                            Text(
                                text = if (isOnline) "En línea" else "Sin conexión",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    // Botón de sincronización
                    IconButton(
                        onClick = {
                            if (isOnline && !isSyncing) {
                                isSyncing = true
                                scope.launch {
                                    val result = syncService.performFullSync()
                                    isSyncing = false

                                    syncMessage = when (result) {
                                        is SyncResult.Success -> {
                                            "✅ Sincronización exitosa\n" +
                                                    "📤 Subidos: ${result.uploaded}\n" +
                                                    "📥 Descargados: ${result.downloaded}\n" +
                                                    "❌ Fallidos: ${result.failed}"
                                        }
                                        is SyncResult.Error -> {
                                            "❌ Error: ${result.message}"
                                        }
                                        SyncResult.NoInternet -> {
                                            "⚠️ Sin conexión a internet"
                                        }
                                    }
                                    showSyncDialog = true

                                    if (result is SyncResult.Success) {
                                        NotificationHelper.showSyncNotification(
                                            context,
                                            products.size
                                        )
                                    }
                                }
                            }
                        },
                        enabled = isOnline && !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sincronizar",
                                tint = if (isOnline) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                            )
                        }
                    }

                    TextButton(onClick = onLogout) {
                        Text(
                            "Salir",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF667eea)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddProduct,
                containerColor = Color(0xFF667eea),
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .shadow(8.dp, shape = CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->

        if (products.isEmpty()) {
            // Estado vacío
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "📚",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay libros aún",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF757575)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Presiona el botón + para agregar tu primer libro",
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 80.dp  // Espacio para el FAB
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onEdit = {
                            scope.launch {
                                // ✅ REGLA DEL EXAMEN: no permitir edición si está "inactivo"
                                if (!product.available) {
                                    val msg = MsgEditService.getMsgEdit()
                                    editBlockedMessage = msg
                                    showEditBlockedDialog = true
                                } else {
                                    onEditProduct(product)
                                }
                            }
                        },
                        onDelete = {
                            selectedProduct = product
                            showDialog = true
                        }
                    )

                }
            }
        }

        // Diálogo de confirmación de eliminación
        if (showDialog && selectedProduct != null) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                    selectedProduct = null
                },
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        "Eliminar libro",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("¿Estás seguro de eliminar \"${selectedProduct?.description}\"?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val productToDelete = selectedProduct
                            showDialog = false
                            selectedProduct = null

                            if (productToDelete != null) {
                                scope.launch {
                                    try {
                                        productViewModel.deleteProduct(productToDelete)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showDialog = false
                        selectedProduct = null
                    }) {
                        Text("Cancelar")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Diálogo de resultado de sincronización
        if (showSyncDialog) {
            AlertDialog(
                onDismissRequest = { showSyncDialog = false },
                icon = {
                    Text("🔄", fontSize = 48.sp)
                },
                title = {
                    Text(
                        "Sincronización",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = { Text(syncMessage) },
                confirmButton = {
                    Button(
                        onClick = { showSyncDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        )
                    ) {
                        Text("Aceptar")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (showEditBlockedDialog) {
            AlertDialog(
                onDismissRequest = { showEditBlockedDialog = false },
                title = { Text("Imposible editar") },
                text = { Text(editBlockedMessage) },
                confirmButton = {
                    Button(onClick = { showEditBlockedDialog = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con título y badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.description,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = if (product.available) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (product.available) "Disponible" else "No disponible",
                        color = if (product.available) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Foto si existe
            if (product.photoUri != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = product.photoUri,
                        contentDescription = "Foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información en grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoRow(icon = "📖", label = "Código", value = product.code)
                InfoRow(icon = "✍️", label = "Autor", value = product.author)
                InfoRow(icon = "📚", label = "Categoría", value = product.category)
                InfoRow(icon = "📅", label = "Fecha", value = product.manufactureDate)
                InfoRow(
                    icon = "💰",
                    label = "Precio",
                    value = "$${String.format("%.2f", product.cost)}",
                    valueColor = Color(0xFF667eea)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(8.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF667eea)
                    )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: String,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF424242)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            modifier = Modifier.width(24.dp)
        )
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}