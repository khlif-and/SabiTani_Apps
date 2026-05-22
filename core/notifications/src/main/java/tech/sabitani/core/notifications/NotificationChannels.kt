package tech.sabitani.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService

object NotificationChannels {
    const val GENERAL_ID = "general"
    private const val GENERAL_NAME = "Umum"
    private const val GENERAL_DESCRIPTION = "Notifikasi umum aplikasi"

    fun ensureChannels(context: Context) {
        val manager = context.getSystemService<NotificationManager>() ?: return
        val general =
            NotificationChannel(
                GENERAL_ID,
                GENERAL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = GENERAL_DESCRIPTION
            }
        manager.createNotificationChannel(general)
    }
}
