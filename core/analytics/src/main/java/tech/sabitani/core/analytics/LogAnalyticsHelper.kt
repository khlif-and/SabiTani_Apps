package tech.sabitani.core.analytics

import timber.log.Timber
import javax.inject.Inject

internal class LogAnalyticsHelper
    @Inject
    constructor() : AnalyticsHelper {
        override fun logEvent(event: AnalyticsEvent) {
            Timber.tag("Analytics").d("event=%s params=%s", event.type, event.params)
        }
    }
