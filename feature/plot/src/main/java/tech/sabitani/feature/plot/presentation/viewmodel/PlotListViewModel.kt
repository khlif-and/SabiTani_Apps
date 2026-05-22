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
import tech.sabitani.feature.plot.domain.usecase.AddPlotUseCase
import tech.sabitani.feature.plot.domain.usecase.ObservePlotsUseCase
import tech.sabitani.feature.plot.presentation.screen.PlotListRoute
import tech.sabitani.feature.plot.presentation.state.PlotListEffect
import tech.sabitani.feature.plot.presentation.state.PlotListIntent
import tech.sabitani.feature.plot.presentation.state.PlotListState

@HiltViewModel
internal class PlotListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePlotsUseCase: ObservePlotsUseCase,
    private val addPlotUseCase: AddPlotUseCase,
) : ViewModel(), ContainerHost<PlotListState, PlotListEffect> {

    private val route = savedStateHandle.toRoute<PlotListRoute>()

    override val container = container<PlotListState, PlotListEffect>(
        PlotListState(farmId = route.farmId, farmName = route.farmName),
    ) {
        observePlotsUseCase(route.farmId)
            .onEach { plots -> intent { reduce { state.copy(isLoading = false, plots = plots) } } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: PlotListIntent) {
        when (intent) {
            PlotListIntent.OpenAddDialog -> openDialog()
            PlotListIntent.DismissAddDialog -> dismissDialog()
            is PlotListIntent.NameChanged -> reduceState { it.copy(draftName = intent.value) }
            is PlotListIntent.AreaChanged -> reduceState { it.copy(draftAreaText = intent.value) }
            is PlotListIntent.SoilTypeChanged -> reduceState { it.copy(draftSoilType = intent.value) }
            is PlotListIntent.IrrigationTypeChanged ->
                reduceState { it.copy(draftIrrigationType = intent.value) }
            is PlotListIntent.NotesChanged -> reduceState { it.copy(draftNotes = intent.value) }
            PlotListIntent.SubmitAddPlot -> submit()
            is PlotListIntent.PlotClicked ->
                intent { postSideEffect(PlotListEffect.NavigateToPlotDetail(intent.plotId)) }
        }
    }

    private fun openDialog() = reduceState {
        it.copy(
            isAddDialogVisible = true,
            draftName = "",
            draftAreaText = "",
            draftNotes = "",
        )
    }

    private fun dismissDialog() = reduceState { it.copy(isAddDialogVisible = false) }

    private fun reduceState(block: (PlotListState) -> PlotListState) = intent {
        reduce { block(state) }
    }

    private fun submit() = intent {
        if (state.isSubmitting) return@intent
        val area = state.draftAreaText.trim().replace(',', '.').toDoubleOrNull()
        if (area == null || area <= 0.0) {
            postSideEffect(PlotListEffect.ShowError("Luas petak harus angka lebih dari 0."))
            return@intent
        }
        reduce { state.copy(isSubmitting = true) }
        val result = addPlotUseCase(
            farmId = state.farmId,
            name = state.draftName,
            areaSqM = area,
            soilType = state.draftSoilType,
            irrigationType = state.draftIrrigationType,
            notes = state.draftNotes,
        )
        result.fold(
            onSuccess = {
                reduce { state.copy(isSubmitting = false, isAddDialogVisible = false) }
            },
            onFailure = { error ->
                reduce { state.copy(isSubmitting = false) }
                postSideEffect(PlotListEffect.ShowError(error.message ?: "Gagal menambah petak."))
            },
        )
    }
}
