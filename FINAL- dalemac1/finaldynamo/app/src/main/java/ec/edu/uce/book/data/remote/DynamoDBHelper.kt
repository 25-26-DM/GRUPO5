package ec.edu.uce.book.data.remote

import android.content.Context
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import ec.edu.uce.book.data.entity.ProductDynamoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression

class DynamoDBHelper(context: Context) {

    private val dynamoDBClient: AmazonDynamoDBClient
    private val dynamoDBMapper: DynamoDBMapper

    init {
        // IMPORTANTE: Reemplaza con tus credenciales de AWS
        // NUNCA compartas estas credenciales públicamente
        val credentials = BasicAWSCredentials(
            "corto",  // ← CAMBIAR
            "largo"   // ← CAMBIAR
        )

        dynamoDBClient = AmazonDynamoDBClient(credentials)
        dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_1)) // ← Cambiar según tu región

        dynamoDBMapper = DynamoDBMapper.builder()
            .dynamoDBClient(dynamoDBClient)
            .build()
    }

    /**
     * Guarda un producto en DynamoDB
     */
    suspend fun saveProduct(product: ProductDynamoModel): Boolean = withContext(Dispatchers.IO) {
        try {
            dynamoDBMapper.save(product)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene todos los productos de DynamoDB
     */


    suspend fun getAllProducts(): List<ProductDynamoModel> = withContext(Dispatchers.IO) {
        try {
            val scanExpression = DynamoDBScanExpression()
            val scanResult = dynamoDBMapper.scan(ProductDynamoModel::class.java, scanExpression)

            println("✅ DYNAMO SCAN OK -> size=${scanResult.size}")
            scanResult.toList()
        } catch (e: Exception) {
            println("❌ DYNAMO SCAN ERROR")
            e.printStackTrace()
            emptyList()
        }
    }


    /**
     * Elimina un producto de DynamoDB
     */
    suspend fun deleteProduct(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val product = ProductDynamoModel()
            product.id = productId
            dynamoDBMapper.delete(product)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene un producto específico de DynamoDB
     */
    suspend fun getProduct(productId: String): ProductDynamoModel? = withContext(Dispatchers.IO) {
        try {
            dynamoDBMapper.load(ProductDynamoModel::class.java, productId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
