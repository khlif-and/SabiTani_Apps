package tech.sabitani.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tech.sabitani.core.notifications.NotificationChannels
import timber.log.Timber

@HiltAndroidApp
class SabiTaniApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NotificationChannels.ensureChannels(this)
    }
}
