package tech.sabitani.feature.onboarding.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.sabitani.feature.onboarding.domain.repository.OnboardingPreferencesRepository
import javax.inject.Inject

private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

internal class OnboardingPreferencesRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : OnboardingPreferencesRepository {
        override fun isOnboardingCompleted(): Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

        override suspend fun setOnboardingCompleted() {
            dataStore.edit { it[ONBOARDING_COMPLETED] = true }
        }
    }
