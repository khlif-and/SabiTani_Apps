package tech.sabitani.feature.plot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.plot.domain.usecase.AddFarmUseCase
import tech.sabitani.feature.plot.domain.usecase.ObserveFarmsUseCase
import tech.sabitani.feature.plot.presentation.state.FarmListEffect
import tech.sabitani.feature.plot.presentation.state.FarmListIntent
import tech.sabitani.feature.plot.presentation.state.FarmListState
import javax.inject.Inject

@HiltViewModel
internal class FarmListViewModel
    @Inject
    constructor(
        private val observeFarmsUseCase: ObserveFarmsUseCase,
        private val addFarmUseCase: AddFarmUseCase,
    ) : ViewModel(),
        ContainerHost<FarmListState, FarmListEffect> {
        override val container =
            container<FarmListState, FarmListEffect>(FarmListState()) {
                observeFarmsUseCase()
                    .onEach { farms -> intent { reduce { state.copy(isLoading = false, farms = farms) } } }
                    .launchIn(viewModelScope)
            }

        fun onIntent(intent: FarmListIntent) {
            when (intent) {
                FarmListIntent.OpenAddDialog -> openDialog()
                FarmListIntent.DismissAddDialog -> dismissDialog()
                is FarmListIntent.NameChanged -> updateName(intent.value)
                is FarmListIntent.LocationChanged -> updateLocation(intent.value)
                is FarmListIntent.TotalAreaChanged -> updateArea(intent.value)
                FarmListIntent.SubmitAddFarm -> submit()
                is FarmListIntent.FarmClicked -> navigateToPlots(intent.farmId)
            }
        }

        private fun openDialog() =
            intent {
                reduce {
                    state.copy(
                        isAddDialogVisible = true,
                        draftName = "",
                        draftLocation = "",
                        draftTotalAreaText = "",
                    )
                }
            }

        private fun dismissDialog() =
            intent {
                reduce { state.copy(isAddDialogVisible = false) }
            }

        private fun updateName(value: String) =
            intent {
                reduce { state.copy(draftName = value) }
            }

        private fun updateLocation(value: String) =
            intent {
                reduce { state.copy(draftLocation = value) }
            }

        private fun updateArea(value: String) =
            intent {
                reduce { state.copy(draftTotalAreaText = value) }
            }

        private fun submit() =
            intent {
                if (state.isSubmitting) return@intent
                reduce { state.copy(isSubmitting = true) }
                val areaValue =
                    state.draftTotalAreaText
                        .trim()
                        .replace(',', '.')
                        .takeIf(String::isNotEmpty)
                        ?.toDoubleOrNull()
                val result =
                    addFarmUseCase(
                        name = state.draftName,
                        location = state.draftLocation,
                        totalAreaSqM = areaValue,
                    )
                result.fold(
                    onSuccess = {
                        reduce { state.copy(isSubmitting = false, isAddDialogVisible = false) }
                    },
                    onFailure = { error ->
                        reduce { state.copy(isSubmitting = false) }
                        postSideEffect(FarmListEffect.ShowError(error.message ?: "Gagal menambah kebun."))
                    },
                )
            }

        private fun navigateToPlots(farmId: Long) =
            intent {
                val farm = state.farms.firstOrNull { it.id == farmId } ?: return@intent
                postSideEffect(FarmListEffect.NavigateToPlotList(farmId = farmId, farmName = farm.name))
            }
    }
