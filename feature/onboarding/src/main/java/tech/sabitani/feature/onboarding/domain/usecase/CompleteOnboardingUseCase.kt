package tech.sabitani.feature.onboarding.domain.usecase

import tech.sabitani.feature.onboarding.domain.repository.OnboardingPreferencesRepository
import javax.inject.Inject

class CompleteOnboardingUseCase
    @Inject
    constructor(
        private val repository: OnboardingPreferencesRepository,
    ) {
        suspend operator fun invoke() = repository.setOnboardingCompleted()
    }
