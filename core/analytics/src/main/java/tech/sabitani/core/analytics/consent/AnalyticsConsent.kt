package tech.sabitani.core.analytics.consent

import kotlinx.coroutines.flow.Flow

interface AnalyticsConsent {
    val isEnabled: Flow<Boolean>

    suspend fun isEnabledNow(): Boolean

    suspend fun setEnabled(enabled: Boolean)
}
