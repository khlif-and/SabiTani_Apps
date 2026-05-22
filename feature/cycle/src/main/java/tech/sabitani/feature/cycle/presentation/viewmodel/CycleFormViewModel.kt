package tech.sabitani.feature.cycle.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.cycle.domain.usecase.StartCycleUseCase
import tech.sabitani.feature.cycle.presentation.screen.CycleFormRoute
import tech.sabitani.feature.cycle.presentation.state.CycleFormEffect
import tech.sabitani.feature.cycle.presentation.state.CycleFormIntent
import tech.sabitani.feature.cycle.presentation.state.CycleFormState

@HiltViewModel
internal class CycleFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val startCycleUseCase: StartCycleUseCase,
    private val clock: Clock,
) : ViewModel(), ContainerHost<CycleFormState, CycleFormEffect> {

    private val route = savedStateHandle.toRoute<CycleFormRoute>()

    override val container = container<CycleFormState, CycleFormEffect>(
        CycleFormState(
            plotId = route.plotId,
            startDate = clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        ),
    )

    fun onIntent(action: CycleFormIntent) {
        when (action) {
            is CycleFormIntent.CommodityChanged -> reduce { it.copy(commodity = action.value) }
            is CycleFormIntent.VarietyChanged -> reduce { it.copy(variety = action.value) }
            is CycleFormIntent.StartDateChanged -> reduce { it.copy(startDate = action.value) }
            is CycleFormIntent.TargetHarvestChanged ->
                reduce { it.copy(targetHarvestDate = action.value) }
            is CycleFormIntent.NotesChanged -> reduce { it.copy(notes = action.value) }
            CycleFormIntent.Submit -> submit()
        }
    }

    private fun reduce(block: (CycleFormState) -> CycleFormState) = intent {
        reduce { block(state) }
    }

    private fun submit() = intent {
        if (state.isSubmitting) return@intent
        val startDate = state.startDate
        if (startDate == null) {
            postSideEffect(CycleFormEffect.ShowError("Tanggal tanam wajib diisi."))
            return@intent
        }
        reduce { state.copy(isSubmitting = true) }
        val result = startCycleUseCase(
            plotId = state.plotId,
            commodity = state.commodity,
            variety = state.variety,
            startDate = startDate,
            targetHarvestDate = state.targetHarvestDate,
            notes = state.notes,
        )
        reduce { state.copy(isSubmitting = false) }
        result.fold(
            onSuccess = { id -> postSideEffect(CycleFormEffect.Submitted(id)) },
            onFailure = { error ->
                postSideEffect(CycleFormEffect.ShowError(error.message ?: "Gagal memulai siklus."))
            },
        )
    }
}
