package tech.sabitani.feature.onboarding.domain.usecase

import javax.inject.Inject
import tech.sabitani.feature.onboarding.domain.repository.OnboardingPreferencesRepository

class CompleteOnboardingUseCase @Inject constructor(
    private val repository: OnboardingPreferencesRepository,
) {
    suspend operator fun invoke() = repository.setOnboardingCompleted()
}
