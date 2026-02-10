package ec.edu.uce.book.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object MsgEditService {

    private const val MSGEDIT_URL =
        "https://api.mockapi.com/api/v1/msgedit/1"


    private const val API_KEY =
        "f5e36b8a0508421fa65b29d09be6f3c6"

    suspend fun getMsgEdit(): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(MSGEDIT_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("X-API-Key", API_KEY)
                connectTimeout = 8000
                readTimeout = 8000
            }

            val response = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            val json = JSONObject(response)
            json.optString("message", "Imposible editar libro.")
        } catch (e: Exception) {
            "Imposible editar libro."
        }
    }
}

