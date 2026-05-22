package tech.sabitani.feature.cycle.presentation.screen

import kotlinx.serialization.Serializable

@Serializable
data class CycleFormRoute(
    val plotId: Long,
)

@Serializable
data class CycleDetailRoute(
    val cycleId: Long,
)
