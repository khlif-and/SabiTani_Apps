package tech.sabitani.feature.plot.presentation.state

import tech.sabitani.core.model.Plot

data class PlotDetailState(
    val plotId: Long = 0L,
    val isLoading: Boolean = true,
    val plot: Plot? = null,
)

sealed interface PlotDetailIntent {
    data object StartCycleClicked : PlotDetailIntent
}

sealed interface PlotDetailEffect {
    data class NavigateToStartCycle(val plotId: Long) : PlotDetailEffect
}
