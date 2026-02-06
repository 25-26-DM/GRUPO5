package ec.edu.uce.book.data.remote

import android.content.Context
import android.net.Uri
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class S3Helper(private val context: Context) {

    private val bucketName = "bookstore-uce-images"
    private val region = Regions.US_EAST_1

    private val s3Client: AmazonS3Client

    init {

        val credentials = BasicAWSCredentials(
            "corto",
            "largo"
        )

        s3Client = AmazonS3Client(credentials)
        s3Client.setRegion(Region.getRegion(region))
    }

    suspend fun uploadImage(fileUri: Uri): String? =
        withContext(Dispatchers.IO) {

            try {

                val file = uriToFile(context, fileUri)

                val fileName = "products/${UUID.randomUUID()}.jpg"

                s3Client.putObject(bucketName, fileName, file)

                return@withContext "https://$bucketName.s3.amazonaws.com/$fileName"

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    private fun uriToFile(context: Context, uri: Uri): File {

        val input = context.contentResolver.openInputStream(uri)
            ?: throw Exception("No se pudo abrir el URI")

        val file = File.createTempFile("upload_", ".jpg", context.cacheDir)

        file.outputStream().use { output ->
            input.copyTo(output)
        }

        return file
    }

}
