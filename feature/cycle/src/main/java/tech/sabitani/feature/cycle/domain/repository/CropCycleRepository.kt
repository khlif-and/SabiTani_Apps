package tech.sabitani.feature.cycle.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.CropCycle
import tech.sabitani.core.model.CycleStatus

interface CropCycleRepository {
    fun observeCyclesByPlot(plotId: Long): Flow<List<CropCycle>>

    fun observeCycle(id: Long): Flow<CropCycle?>

    suspend fun addCycle(
        plotId: Long,
        commodity: String,
        variety: String?,
        startDate: LocalDate,
        targetHarvestDate: LocalDate?,
        notes: String?,
    ): Long

    suspend fun updateStatus(
        id: Long,
        status: CycleStatus,
        actualHarvestDate: LocalDate?,
    )
}
