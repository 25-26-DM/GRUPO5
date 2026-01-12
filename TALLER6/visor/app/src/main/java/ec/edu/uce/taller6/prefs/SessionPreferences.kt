package ec.edu.uce.taller6.prefs

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class SessionPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

    fun saveLogin(username: String) {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val count = prefs.getInt("login_count", 0) + 1

        prefs.edit()
            .putString("username_$count", username)
            .putString("login_time_$count", date)
            .putInt("login_count", count)
            .apply()
    }
}
