package tech.sabitani.feature.cycle.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.dao.FarmActivityDao
import tech.sabitani.core.model.ActivityType
import tech.sabitani.core.model.FarmActivity
import tech.sabitani.feature.cycle.data.mapper.newFarmActivityEntity
import tech.sabitani.feature.cycle.data.mapper.toDomain
import tech.sabitani.feature.cycle.domain.repository.FarmActivityRepository

internal class FarmActivityRepositoryImpl @Inject constructor(
    private val activityDao: FarmActivityDao,
    private val clock: Clock,
) : FarmActivityRepository {

    override fun observeActivities(cycleId: Long): Flow<List<FarmActivity>> =
        activityDao.observeByCycle(cycleId).map { list -> list.map { it.toDomain() } }

    override suspend fun addActivity(
        cycleId: Long,
        type: ActivityType,
        performedOn: LocalDate,
        material: String?,
        dosage: String?,
        notes: String?,
    ): Long = activityDao.insert(
        newFarmActivityEntity(
            cycleId = cycleId,
            type = type,
            performedOn = performedOn,
            material = material,
            dosage = dosage,
            notes = notes,
            createdAt = clock.now(),
        ),
    )

    override suspend fun deleteActivity(id: Long) {
        activityDao.deleteById(id)
    }
}
