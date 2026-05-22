package tech.sabitani.feature.plot.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import tech.sabitani.core.database.dao.PlotDao
import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.Plot
import tech.sabitani.core.model.SoilType
import tech.sabitani.feature.plot.data.mapper.newPlotEntity
import tech.sabitani.feature.plot.data.mapper.toDomain
import tech.sabitani.feature.plot.domain.repository.PlotRepository

internal class PlotRepositoryImpl @Inject constructor(
    private val plotDao: PlotDao,
    private val clock: Clock,
) : PlotRepository {

    override fun observePlots(farmId: Long): Flow<List<Plot>> =
        plotDao.observeByFarm(farmId).map { list -> list.map { it.toDomain() } }

    override fun observePlot(id: Long): Flow<Plot?> =
        plotDao.observeById(id).map { it?.toDomain() }

    override suspend fun addPlot(
        farmId: Long,
        name: String,
        areaSqM: Double,
        soilType: SoilType,
        irrigationType: IrrigationType,
        notes: String?,
    ): Long = plotDao.insert(
        newPlotEntity(
            farmId = farmId,
            name = name,
            areaSqM = areaSqM,
            soilType = soilType,
            irrigationType = irrigationType,
            notes = notes,
            createdAt = clock.now(),
        ),
    )

    override suspend fun deletePlot(id: Long) {
        plotDao.deleteById(id)
    }
}
