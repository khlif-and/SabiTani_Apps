package tech.sabitani.feature.plot.presentation.screen

import kotlinx.serialization.Serializable

@Serializable
data object FarmListRoute

@Serializable
data class PlotListRoute(val farmId: Long, val farmName: String)

@Serializable
data class PlotDetailRoute(val plotId: Long)
