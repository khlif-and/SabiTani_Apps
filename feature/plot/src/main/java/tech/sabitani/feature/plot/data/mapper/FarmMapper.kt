package tech.sabitani.feature.plot.data.mapper

import kotlinx.datetime.Instant
import tech.sabitani.core.database.entity.FarmEntity
import tech.sabitani.core.model.Farm

internal fun FarmEntity.toDomain(): Farm = Farm(
    id = id,
    name = name,
    location = location,
    totalAreaSqM = totalAreaSqM,
    createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
)

internal fun newFarmEntity(
    name: String,
    location: String?,
    totalAreaSqM: Double?,
    createdAt: Instant,
): FarmEntity = FarmEntity(
    name = name,
    location = location,
    totalAreaSqM = totalAreaSqM,
    createdAtEpochMillis = createdAt.toEpochMilliseconds(),
)
