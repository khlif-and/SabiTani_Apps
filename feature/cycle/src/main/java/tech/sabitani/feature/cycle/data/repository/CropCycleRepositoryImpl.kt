package tech.sabitani.feature.cycle.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.dao.CropCycleDao
import tech.sabitani.core.model.CropCycle
import tech.sabitani.core.model.CycleStatus
import tech.sabitani.feature.cycle.data.mapper.newCropCycleEntity
import tech.sabitani.feature.cycle.data.mapper.toDomain
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository
import javax.inject.Inject

internal class CropCycleRepositoryImpl
    @Inject
    constructor(
        private val cycleDao: CropCycleDao,
        private val clock: Clock,
    ) : CropCycleRepository {
        override fun observeCyclesByPlot(plotId: Long): Flow<List<CropCycle>> =
            cycleDao.observeByPlot(plotId).map { list -> list.map { it.toDomain() } }

        override fun observeCycle(id: Long): Flow<CropCycle?> = cycleDao.observeById(id).map { it?.toDomain() }

        override suspend fun addCycle(
            plotId: Long,
            commodity: String,
            variety: String?,
            startDate: LocalDate,
            targetHarvestDate: LocalDate?,
            notes: String?,
        ): Long =
            cycleDao.insert(
                newCropCycleEntity(
                    plotId = plotId,
                    commodity = commodity,
                    variety = variety,
                    startDate = startDate,
                    targetHarvestDate = targetHarvestDate,
                    notes = notes,
                    createdAt = clock.now(),
                ),
            )

        override suspend fun updateStatus(
            id: Long,
            status: CycleStatus,
            actualHarvestDate: LocalDate?,
        ) {
            val current = cycleDao.observeById(id).first() ?: return
            cycleDao.update(
                current.copy(
                    status = status.name,
                    actualHarvestDateIso =
                        actualHarvestDate?.toString()
                            ?: current.actualHarvestDateIso,
                ),
            )
        }
    }
