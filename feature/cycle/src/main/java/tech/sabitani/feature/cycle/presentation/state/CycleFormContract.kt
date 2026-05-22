package tech.sabitani.feature.cycle.presentation.state

import kotlinx.datetime.LocalDate

data class CycleFormState(
    val plotId: Long = 0L,
    val commodity: String = "",
    val variety: String = "",
    val startDate: LocalDate? = null,
    val targetHarvestDate: LocalDate? = null,
    val notes: String = "",
    val isSubmitting: Boolean = false,
)

sealed interface CycleFormIntent {
    data class CommodityChanged(
        val value: String,
    ) : CycleFormIntent

    data class VarietyChanged(
        val value: String,
    ) : CycleFormIntent

    data class StartDateChanged(
        val value: LocalDate,
    ) : CycleFormIntent

    data class TargetHarvestChanged(
        val value: LocalDate?,
    ) : CycleFormIntent

    data class NotesChanged(
        val value: String,
    ) : CycleFormIntent

    data object Submit : CycleFormIntent
}

sealed interface CycleFormEffect {
    data class ShowError(
        val message: String,
    ) : CycleFormEffect

    data class Submitted(
        val cycleId: Long,
    ) : CycleFormEffect
}
