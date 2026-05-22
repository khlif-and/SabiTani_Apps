package tech.sabitani.feature.onboarding.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingPreferencesRepository {
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
