package tech.sabitani.core.analytics

import tech.sabitani.core.analytics.crashlytics.CrashlyticsBridge
import timber.log.Timber
import javax.inject.Inject

internal class CrashlyticsAnalyticsHelper
    @Inject
    constructor(
        private val crashlyticsBridge: CrashlyticsBridge,
    ) : AnalyticsHelper {
        override fun logEvent(event: AnalyticsEvent) {
            Timber.tag(TAG).d("event=%s params=%s", event.type, event.params)
            crashlyticsBridge.logEvent(event)
        }

        private companion object {
            const val TAG = "Analytics"
        }
    }
