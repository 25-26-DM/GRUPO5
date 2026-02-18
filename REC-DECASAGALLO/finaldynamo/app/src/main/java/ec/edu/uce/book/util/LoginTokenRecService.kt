package ec.edu.uce.book.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object LoginTokenRecService {

    // ✅ CAMBIA ESTA URL por la Function URL (o API Gateway) de tu servicio logintokenrec
    private const val BASE_URL =
        "https://6k3qox6cew5xwmkm3xrtwcr4j40zxlej.lambda-url.us-east-1.on.aws/"

    /**
     * Paso 1: pedir que envíe el token al correo (6 dígitos).
     * Esperado: status 200 si se envió.
     */
    suspend fun requestToken(email: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val url = URL(BASE_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
            }

            val payload = JSONObject()
                .put("action", "REQUEST") // 🔥 importante para que tu lambda sepa qué hacer
                .put("email", email)
                .toString()

            conn.outputStream.use { os -> os.write(payload.toByteArray()) }

            val code = conn.responseCode
            val body = readResponse(conn)

            if (code in 200..299) {
                // opcional: si tu lambda devuelve algún message, lo mostramos
                val msg = try { JSONObject(body).optString("message", "Código enviado") } catch (e: Exception) { "Código enviado" }
                Result.success(msg)
            } else {
                Result.failure(Exception("HTTP $code -> $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Paso 2: validar el token ingresado.
     * Esperado: status 200 si es válido.
     */
    suspend fun verifyToken(email: String, token: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = URL(BASE_URL)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                doOutput = true
            }

            val payload = JSONObject()
                .put("action", "VERIFY")
                .put("email", email)
                .put("token", token)
                .toString()

            conn.outputStream.use { os -> os.write(payload.toByteArray()) }

            val code = conn.responseCode
            val body = readResponse(conn)

            if (code in 200..299) {
                // tu lambda puede devolver ok=true / valid=true / etc
                val ok = try {
                    val json = JSONObject(body)
                    json.optBoolean("ok", json.optBoolean("valid", true))
                } catch (e: Exception) {
                    true
                }
                Result.success(ok)
            } else {
                Result.failure(Exception("HTTP $code -> $body"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun readResponse(conn: HttpURLConnection): String {
        val stream = try {
            if (conn.responseCode >= 400) conn.errorStream else conn.inputStream
        } catch (e: Exception) {
            conn.errorStream
        } ?: return ""

        return BufferedReader(InputStreamReader(stream)).use { it.readText() }
    }
}
