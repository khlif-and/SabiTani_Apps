package tech.sabitani.core.analytics

import javax.inject.Inject
import timber.log.Timber

internal class LogAnalyticsHelper @Inject constructor() : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        Timber.tag("Analytics").d("event=%s params=%s", event.type, event.params)
    }
}
