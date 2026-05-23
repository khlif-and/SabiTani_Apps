package tech.sabitani.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import tech.sabitani.core.analytics.init.AnalyticsInitializer
import tech.sabitani.core.notifications.NotificationChannels
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SabiTaniApp : Application() {
    @Inject
    lateinit var analyticsInitializer: AnalyticsInitializer

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NotificationChannels.ensureChannels(this)
        analyticsInitializer.initialize()
    }
}
