package tech.sabitani.feature.plot.presentation.state

import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.Plot
import tech.sabitani.core.model.SoilType

data class PlotListState(
    val farmId: Long = 0L,
    val farmName: String = "",
    val isLoading: Boolean = true,
    val plots: List<Plot> = emptyList(),
    val isAddDialogVisible: Boolean = false,
    val draftName: String = "",
    val draftAreaText: String = "",
    val draftSoilType: SoilType = SoilType.LOAM,
    val draftIrrigationType: IrrigationType = IrrigationType.RAIN_FED,
    val draftNotes: String = "",
    val isSubmitting: Boolean = false,
)

sealed interface PlotListIntent {
    data object OpenAddDialog : PlotListIntent
    data object DismissAddDialog : PlotListIntent
    data class NameChanged(val value: String) : PlotListIntent
    data class AreaChanged(val value: String) : PlotListIntent
    data class SoilTypeChanged(val value: SoilType) : PlotListIntent
    data class IrrigationTypeChanged(val value: IrrigationType) : PlotListIntent
    data class NotesChanged(val value: String) : PlotListIntent
    data object SubmitAddPlot : PlotListIntent
    data class PlotClicked(val plotId: Long) : PlotListIntent
}

sealed interface PlotListEffect {
    data class ShowError(val message: String) : PlotListEffect
    data class NavigateToPlotDetail(val plotId: Long) : PlotListEffect
}
