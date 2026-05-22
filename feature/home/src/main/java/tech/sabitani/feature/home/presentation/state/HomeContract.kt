package tech.sabitani.feature.home.presentation.state

import tech.sabitani.core.model.DashboardSummary

data class HomeState(
    val isLoading: Boolean = true,
    val summary: DashboardSummary? = null,
    val errorMessage: String? = null,
)

sealed interface HomeIntent {
    data object Refresh : HomeIntent

    data object OpenFarms : HomeIntent

    data object OpenCycles : HomeIntent
}

sealed interface HomeEffect {
    data object NavigateToFarms : HomeEffect

    data object NavigateToCycles : HomeEffect
}
