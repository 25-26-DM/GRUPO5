package ec.edu.uce.book.data.entity

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable

@DynamoDBTable(tableName = "BookStoreProducts")
data class ProductDynamoModel(

    @DynamoDBHashKey(attributeName = "id")
    var id: String = "",

    @DynamoDBAttribute(attributeName = "code")
    var code: String = "",

    @DynamoDBAttribute(attributeName = "description")
    var description: String = "",

    @DynamoDBAttribute(attributeName = "author")
    var author: String = "",

    @DynamoDBAttribute(attributeName = "category")
    var category: String = "",

    @DynamoDBAttribute(attributeName = "manufactureDate")
    var manufactureDate: String = "",

    @DynamoDBAttribute(attributeName = "cost")
    var cost: Double = 0.0,

    @DynamoDBAttribute(attributeName = "available")
    var available: Boolean = true,

    @DynamoDBAttribute(attributeName = "photoUri")
    var photoUri: String? = null,

    @DynamoDBAttribute(attributeName = "lastModified")
    var lastModified: Long = System.currentTimeMillis()
) {
    // Constructor sin argumentos requerido por DynamoDB Mapper
    constructor() : this("", "", "", "", "", "", 0.0, true, null, System.currentTimeMillis())
}
