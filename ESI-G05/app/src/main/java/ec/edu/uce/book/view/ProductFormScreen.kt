package ec.edu.uce.book.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ec.edu.uce.book.controller.ProductController
import ec.edu.uce.book.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productIndex: Int? = null,
    productToEdit: Product? = null,
    onFinish: () -> Unit
) {
    // ✅ CONTROLLER EN MEMORIA
    val controller = remember { ProductController() }

    val initialProduct = productToEdit

    var code by remember { mutableStateOf(initialProduct?.code ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var author by remember { mutableStateOf(initialProduct?.author ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: "") }
    var date by remember { mutableStateOf(initialProduct?.manufactureDate ?: "") }
    var cost by remember { mutableStateOf(initialProduct?.cost?.toString() ?: "") }
    var available by remember { mutableStateOf(initialProduct?.available ?: true) }

    val hasChanges by remember(
        code, description, author, category, date, cost, available
    ) {
        mutableStateOf(
            initialProduct == null ||
                    code != initialProduct.code ||
                    description != initialProduct.description ||
                    author != initialProduct.author ||
                    category != initialProduct.category ||
                    date != initialProduct.manufactureDate ||
                    cost != initialProduct.cost.toString() ||
                    available != initialProduct.available
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (initialProduct == null) "Agregar Libro" else "Editar Libro",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onFinish) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Código") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("Autor") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Fecha de Publicación") },
                        placeholder = { Text("dd/mm/yyyy") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cost,
                        onValueChange = { cost = it },
                        label = { Text("Costo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Disponible", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = available,
                            onCheckedChange = { available = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (hasChanges) {
                        val product = Product(
                            code = code,
                            description = description,
                            author = author,
                            category = category,
                            manufactureDate = date,
                            cost = cost.toDoubleOrNull() ?: 0.0,
                            available = available
                        )

                        if (initialProduct == null) {
                            controller.addProduct(product)
                        } else {
                            controller.updateProduct(productIndex!!, product)
                        }
                    }
                    onFinish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (hasChanges) "Guardar cambios" else "Volver",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
