package ec.edu.uce.book.data.entity

/**
 * Convierte ProductEntity (Room) a ProductDynamoModel (DynamoDB)
 */
fun ProductEntity.toDynamoModel(): ProductDynamoModel {
    return ProductDynamoModel(
        id = this.id.toString(),
        code = this.code,
        description = this.description,
        author = this.author,
        category = this.category,
        manufactureDate = this.manufactureDate,
        cost = this.cost,
        available = this.available,
        photoUri = this.photoUri,
        lastModified = this.lastModified
    )
}


/**
 * Convierte ProductDynamoModel (DynamoDB) a ProductEntity (Room)
 */
fun ProductDynamoModel.toEntity(): ProductEntity {
    return ProductEntity(
        id = this.id.toIntOrNull() ?: 0,
        code = this.code,
        description = this.description,
        author = this.author,
        category = this.category,
        manufactureDate = this.manufactureDate,
        cost = this.cost,
        available = this.available,
        photoUri = this.photoUri
    )
}