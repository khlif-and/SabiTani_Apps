package tech.sabitani.feature.plot.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Farm

interface FarmRepository {
    fun observeFarms(): Flow<List<Farm>>

    fun observeFarm(id: Long): Flow<Farm?>

    suspend fun addFarm(
        name: String,
        location: String?,
        totalAreaSqM: Double?,
    ): Long

    suspend fun deleteFarm(id: Long)
}
