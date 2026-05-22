package tech.sabitani.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import tech.sabitani.feature.onboarding.domain.usecase.CompleteOnboardingUseCase

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {

    fun onGetStartedClicked(onCompleted: () -> Unit) {
        viewModelScope.launch {
            completeOnboardingUseCase()
            onCompleted()
        }
    }
}
