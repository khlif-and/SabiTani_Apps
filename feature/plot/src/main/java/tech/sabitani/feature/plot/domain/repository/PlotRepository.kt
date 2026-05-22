package tech.sabitani.feature.plot.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.Plot
import tech.sabitani.core.model.SoilType

interface PlotRepository {
    fun observePlots(farmId: Long): Flow<List<Plot>>

    fun observePlot(id: Long): Flow<Plot?>

    suspend fun addPlot(
        farmId: Long,
        name: String,
        areaSqM: Double,
        soilType: SoilType,
        irrigationType: IrrigationType,
        notes: String?,
    ): Long

    suspend fun deletePlot(id: Long)
}
