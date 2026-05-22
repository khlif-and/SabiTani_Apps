package tech.sabitani.feature.plot.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.plot.domain.usecase.ObservePlotDetailUseCase
import tech.sabitani.feature.plot.presentation.screen.PlotDetailRoute
import tech.sabitani.feature.plot.presentation.state.PlotDetailEffect
import tech.sabitani.feature.plot.presentation.state.PlotDetailIntent
import tech.sabitani.feature.plot.presentation.state.PlotDetailState

@HiltViewModel
internal class PlotDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePlotDetailUseCase: ObservePlotDetailUseCase,
) : ViewModel(), ContainerHost<PlotDetailState, PlotDetailEffect> {

    private val route = savedStateHandle.toRoute<PlotDetailRoute>()

    override val container = container<PlotDetailState, PlotDetailEffect>(
        PlotDetailState(plotId = route.plotId),
    ) {
        observePlotDetailUseCase(route.plotId)
            .onEach { plot -> intent { reduce { state.copy(isLoading = false, plot = plot) } } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: PlotDetailIntent) {
        when (intent) {
            PlotDetailIntent.StartCycleClicked -> intent {
                postSideEffect(PlotDetailEffect.NavigateToStartCycle(state.plotId))
            }
        }
    }
}
