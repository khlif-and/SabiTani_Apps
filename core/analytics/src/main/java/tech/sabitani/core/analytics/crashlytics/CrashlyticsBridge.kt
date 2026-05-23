package tech.sabitani.core.analytics.crashlytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import tech.sabitani.core.analytics.AnalyticsEvent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashlyticsBridge
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val available: Boolean =
            try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (error: IllegalStateException) {
                Timber.tag(TAG).d(error, "Firebase not available")
                false
            } catch (error: NoClassDefFoundError) {
                Timber.tag(TAG).d(error, "Firebase classes missing")
                false
            }

        val isAvailable: Boolean = available

        fun setCollectionEnabled(enabled: Boolean) {
            if (!available) return
            runCatching {
                FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(enabled)
            }.onFailure { Timber.tag(TAG).w(it, "setCollectionEnabled failed") }
        }

        fun logEvent(event: AnalyticsEvent) {
            if (!available) return
            runCatching {
                FirebaseCrashlytics.getInstance().log("event=${event.type}")
            }.onFailure { Timber.tag(TAG).w(it, "logEvent failed") }
        }

        private companion object {
            const val TAG = "Crashlytics"
        }
    }
