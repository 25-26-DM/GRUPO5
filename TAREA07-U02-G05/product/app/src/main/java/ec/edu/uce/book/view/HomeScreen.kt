package ec.edu.uce.book.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ec.edu.uce.book.controller.ProductController
import ec.edu.uce.book.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (Int, Product) -> Unit
) {
    // ✅ CONTROLLER SOLO EN MEMORIA
    val controller = remember { ProductController() }

    var products by remember { mutableStateOf(controller.getProducts().toList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(

        // 🔷 TOP BAR
        topBar = {
            TopAppBar(
                title = {
                    Text("BookStore UCE", fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },

        // ➕ FAB
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }

    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Listado de Libros",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            itemsIndexed(products) { index, product ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = if (product.available) "Disponible" else "No disponible",
                                color = if (product.available) Color(0xFF2E7D32) else Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text("Código: ${product.code}")
                        Text("Autor: ${product.author}")
                        Text("Categoría: ${product.category}")
                        Text("Fecha publicación: ${product.manufactureDate}")
                        Text("Costo: $${product.cost}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(onClick = {
                                onEditProduct(index, product)
                            }) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = {
                                selectedIndex = index
                                showDialog = true
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        // 🗑️ DIÁLOGO CONFIRMAR ELIMINACIÓN
        if (showDialog && selectedIndex != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Eliminar libro") },
                text = { Text("¿Estás seguro de eliminar este libro?") },
                confirmButton = {
                    TextButton(onClick = {
                        controller.deleteProduct(selectedIndex!!)
                        products = controller.getProducts().toList()
                        showDialog = false
                    }) {
                        Text("Eliminar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
