package tech.sabitani.feature.cycle.data.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.entity.FarmActivityEntity
import tech.sabitani.core.model.ActivityType
import tech.sabitani.core.model.FarmActivity

internal fun FarmActivityEntity.toDomain(): FarmActivity =
    FarmActivity(
        id = id,
        cycleId = cycleId,
        type = runCatching { ActivityType.valueOf(type) }.getOrDefault(ActivityType.OTHER),
        performedOn = LocalDate.parse(performedOnIso),
        material = material,
        dosage = dosage,
        notes = notes,
        createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
    )

internal fun newFarmActivityEntity(
    cycleId: Long,
    type: ActivityType,
    performedOn: LocalDate,
    material: String?,
    dosage: String?,
    notes: String?,
    createdAt: Instant,
): FarmActivityEntity =
    FarmActivityEntity(
        cycleId = cycleId,
        type = type.name,
        performedOnIso = performedOn.toString(),
        material = material,
        dosage = dosage,
        notes = notes,
        createdAtEpochMillis = createdAt.toEpochMilliseconds(),
    )
