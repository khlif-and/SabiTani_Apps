package tech.sabitani.feature.cycle.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.ActivityType
import tech.sabitani.core.model.FarmActivity

interface FarmActivityRepository {
    fun observeActivities(cycleId: Long): Flow<List<FarmActivity>>

    suspend fun addActivity(
        cycleId: Long,
        type: ActivityType,
        performedOn: LocalDate,
        material: String?,
        dosage: String?,
        notes: String?,
    ): Long

    suspend fun deleteActivity(id: Long)
}
