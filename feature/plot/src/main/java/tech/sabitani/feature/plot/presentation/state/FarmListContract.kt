package tech.sabitani.feature.plot.presentation.state

import tech.sabitani.core.model.Farm

data class FarmListState(
    val isLoading: Boolean = true,
    val farms: List<Farm> = emptyList(),
    val isAddDialogVisible: Boolean = false,
    val draftName: String = "",
    val draftLocation: String = "",
    val draftTotalAreaText: String = "",
    val isSubmitting: Boolean = false,
)

sealed interface FarmListIntent {
    data object OpenAddDialog : FarmListIntent
    data object DismissAddDialog : FarmListIntent
    data class NameChanged(val value: String) : FarmListIntent
    data class LocationChanged(val value: String) : FarmListIntent
    data class TotalAreaChanged(val value: String) : FarmListIntent
    data object SubmitAddFarm : FarmListIntent
    data class FarmClicked(val farmId: Long) : FarmListIntent
}

sealed interface FarmListEffect {
    data class ShowError(val message: String) : FarmListEffect
    data class NavigateToPlotList(val farmId: Long, val farmName: String) : FarmListEffect
}
