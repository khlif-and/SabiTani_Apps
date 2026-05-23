package tech.sabitani.core.analytics.init

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import tech.sabitani.core.analytics.consent.AnalyticsConsent
import tech.sabitani.core.analytics.crashlytics.CrashlyticsBridge
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsInitializer
    @Inject
    constructor(
        private val consent: AnalyticsConsent,
        private val crashlyticsBridge: CrashlyticsBridge,
    ) {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        fun initialize() {
            scope.launch {
                consent.isEnabled.distinctUntilChanged().collect { enabled ->
                    crashlyticsBridge.setCollectionEnabled(enabled)
                }
            }
        }
    }
