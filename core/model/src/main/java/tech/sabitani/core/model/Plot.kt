package tech.sabitani.core.model

import kotlinx.datetime.Instant

data class Plot(
    val id: Long = 0L,
    val farmId: Long,
    val name: String,
    val areaSqM: Double,
    val soilType: SoilType,
    val irrigationType: IrrigationType,
    val notes: String?,
    val createdAt: Instant,
)
