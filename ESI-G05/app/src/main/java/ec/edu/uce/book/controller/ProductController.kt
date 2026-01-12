package ec.edu.uce.book.controller

import ec.edu.uce.book.data.MemoryData
import ec.edu.uce.book.model.Product

class ProductController {

    fun getProducts(): List<Product> = MemoryData.products

    fun addProduct(product: Product) {
        MemoryData.products.add(product)
    }

    fun updateProduct(index: Int, product: Product) {
        MemoryData.products[index] = product
    }

    fun deleteProduct(index: Int) {
        MemoryData.products.removeAt(index)
    }

}
