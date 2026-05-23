package tech.sabitani.core.analytics.consent

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DataStoreAnalyticsConsent
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : AnalyticsConsent {
        override val isEnabled: Flow<Boolean> =
            dataStore.data.map { prefs -> prefs[KEY_ANALYTICS_CONSENT] ?: false }

        override suspend fun isEnabledNow(): Boolean = isEnabled.first()

        override suspend fun setEnabled(enabled: Boolean) {
            dataStore.edit { prefs -> prefs[KEY_ANALYTICS_CONSENT] = enabled }
        }

        private companion object {
            val KEY_ANALYTICS_CONSENT = booleanPreferencesKey("analytics_consent_enabled")
        }
    }
