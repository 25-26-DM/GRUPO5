package ec.edu.uce.book.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ec.edu.uce.book.R

object NotificationHelper {
    private const val CHANNEL_ID = "product_sync_channel"
    private const val CHANNEL_NAME = "Sincronización de Productos"
    private const val CHANNEL_DESCRIPTION = "Notificaciones sobre sincronización de productos"
    private const val NOTIFICATION_ID = 1001

    /**
     * Crea el canal de notificaciones (requerido para Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra notificación de sincronización completada
     */
    fun showSyncNotification(context: Context, productCount: Int) {
        // Crear el canal si no existe
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Usa el ícono de la app
            .setContentTitle("Sincronización Completa")
            .setContentText("$productCount producto(s) almacenado(s) localmente")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Se cierra al tocar
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // El permiso de notificaciones no fue otorgado
            e.printStackTrace()
        }
    }

    /**
     * Muestra notificación de producto agregado
     */
    fun showProductAddedNotification(context: Context, productName: String, totalProducts: Int) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Producto Agregado")
            .setContentText("\"$productName\" agregado. Total: $totalProducts producto(s)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Muestra notificación de producto eliminado
     */
    fun showProductDeletedNotification(context: Context, productName: String, totalProducts: Int) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Producto Eliminado")
            .setContentText("\"$productName\" eliminado. Total: $totalProducts producto(s)")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}