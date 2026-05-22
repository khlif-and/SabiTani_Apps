package tech.sabitani.feature.cycle.data.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.entity.CropCycleEntity
import tech.sabitani.core.model.CropCycle
import tech.sabitani.core.model.CycleStatus

internal fun CropCycleEntity.toDomain(): CropCycle =
    CropCycle(
        id = id,
        plotId = plotId,
        commodity = commodity,
        variety = variety,
        startDate = LocalDate.parse(startDateIso),
        targetHarvestDate = targetHarvestDateIso?.let(LocalDate::parse),
        actualHarvestDate = actualHarvestDateIso?.let(LocalDate::parse),
        status = runCatching { CycleStatus.valueOf(status) }.getOrDefault(CycleStatus.ACTIVE),
        notes = notes,
        createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
    )

internal fun newCropCycleEntity(
    plotId: Long,
    commodity: String,
    variety: String?,
    startDate: LocalDate,
    targetHarvestDate: LocalDate?,
    notes: String?,
    createdAt: Instant,
): CropCycleEntity =
    CropCycleEntity(
        plotId = plotId,
        commodity = commodity,
        variety = variety,
        startDateIso = startDate.toString(),
        targetHarvestDateIso = targetHarvestDate?.toString(),
        actualHarvestDateIso = null,
        status = CycleStatus.ACTIVE.name,
        notes = notes,
        createdAtEpochMillis = createdAt.toEpochMilliseconds(),
    )
