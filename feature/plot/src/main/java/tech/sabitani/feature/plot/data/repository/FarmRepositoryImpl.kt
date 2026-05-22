package tech.sabitani.feature.plot.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import tech.sabitani.core.database.dao.FarmDao
import tech.sabitani.core.model.Farm
import tech.sabitani.feature.plot.data.mapper.newFarmEntity
import tech.sabitani.feature.plot.data.mapper.toDomain
import tech.sabitani.feature.plot.domain.repository.FarmRepository

internal class FarmRepositoryImpl @Inject constructor(
    private val farmDao: FarmDao,
    private val clock: Clock,
) : FarmRepository {

    override fun observeFarms(): Flow<List<Farm>> =
        farmDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFarm(id: Long): Flow<Farm?> =
        farmDao.observeById(id).map { it?.toDomain() }

    override suspend fun addFarm(name: String, location: String?, totalAreaSqM: Double?): Long =
        farmDao.insert(
            newFarmEntity(
                name = name,
                location = location,
                totalAreaSqM = totalAreaSqM,
                createdAt = clock.now(),
            ),
        )

    override suspend fun deleteFarm(id: Long) {
        farmDao.deleteById(id)
    }
}
