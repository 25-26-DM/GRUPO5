package ec.edu.uce.book.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.uce.book.data.entity.ProductEntity
import ec.edu.uce.book.data.repository.ProductRepository
import ec.edu.uce.book.util.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProductViewModel(
    application: Application,
    val repository: ProductRepository
) : AndroidViewModel(application)
 {

    val products = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        syncNow()
        viewModelScope.launch {
            val currentProducts = repository.allProducts.first()
            if (currentProducts.isEmpty()) {
                val initialProducts = listOf(
                    ProductEntity(
                        code = "B001",
                        description = "Cien años de soledad",
                        author = "Gabriel Garcia Marquez",
                        category = "Novela",
                        manufactureDate = "01/01/1967",
                        cost = 12.5,
                        available = true,
                        photoUri = null
                    ),
                    ProductEntity(
                        code = "B002",
                        description = "Orgullo y prejuicio",
                        author = "Jane Austen",
                        category = "Novela",
                        manufactureDate = "05/02/1813",
                        cost = 15.0,
                        available = true,
                        photoUri = null
                    ),
                    ProductEntity(
                        code = "B003",
                        description = "El ingenioso hidalgo Don Quijote de la Mancha",
                        author = "Autor C",
                        category = "Historia",
                        manufactureDate = "10/03/1605",
                        cost = 20.0,
                        available = false,
                        photoUri = null
                    )
                )

                initialProducts.forEach { repository.addProduct(it) }

                // Notificación inicial
                NotificationHelper.showSyncNotification(
                    getApplication(),
                    initialProducts.size
                )
            }
        }
    }

     fun addProduct(product: ProductEntity) {
         viewModelScope.launch {
             // 1) Agrega (Room + Dynamo si hay internet)
             repository.addProduct(product)

             // 2) Sincroniza DESPUÉS de agregar
             syncNow()

             // 3) Notificación con total real
             val totalProducts = repository.allProducts.first().size
             NotificationHelper.showProductAddedNotification(
                 getApplication(),
                 product.description,
                 totalProducts
             )
         }
     }

     fun syncNow() {
         viewModelScope.launch {
             // 1) Subir pendientes local -> Dynamo
             val uploaded = repository.syncPending()

             // 2) Bajar Dynamo -> local (Room)
             val downloaded = repository.pullFromDynamoToLocal()

             // 3) Notificación si hubo cambios
             if (uploaded > 0 || downloaded > 0) {
                 val totalProducts = repository.allProducts.first().size
                 NotificationHelper.showSyncNotification(
                     getApplication(),
                     totalProducts
                 )
             }
         }
     }




     fun updateProduct(product: ProductEntity) {
         viewModelScope.launch {
             repository.updateProduct(product)

             // sincroniza después
             syncNow()

             val totalProducts = repository.allProducts.first().size
             NotificationHelper.showSyncNotification(
                 getApplication(),
                 totalProducts
             )
         }
     }


     fun deleteProduct(product: ProductEntity) {
         viewModelScope.launch {
             try {
                 repository.deleteProduct(product)

                 // sincroniza después
                 syncNow()

                 val totalProducts = repository.allProducts.first().size

                 try {
                     NotificationHelper.showProductDeletedNotification(
                         getApplication(),
                         product.description,
                         totalProducts
                     )
                 } catch (e: Exception) {
                     e.printStackTrace()
                 }

             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }
     }


}