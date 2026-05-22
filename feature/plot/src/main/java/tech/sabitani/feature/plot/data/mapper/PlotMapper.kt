package tech.sabitani.feature.plot.data.mapper

import kotlinx.datetime.Instant
import tech.sabitani.core.database.entity.PlotEntity
import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.Plot
import tech.sabitani.core.model.SoilType

internal fun PlotEntity.toDomain(): Plot = Plot(
    id = id,
    farmId = farmId,
    name = name,
    areaSqM = areaSqM,
    soilType = runCatching { SoilType.valueOf(soilType) }.getOrDefault(SoilType.OTHER),
    irrigationType = runCatching { IrrigationType.valueOf(irrigationType) }
        .getOrDefault(IrrigationType.OTHER),
    notes = notes,
    createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
)

internal fun newPlotEntity(
    farmId: Long,
    name: String,
    areaSqM: Double,
    soilType: SoilType,
    irrigationType: IrrigationType,
    notes: String?,
    createdAt: Instant,
): PlotEntity = PlotEntity(
    farmId = farmId,
    name = name,
    areaSqM = areaSqM,
    soilType = soilType.name,
    irrigationType = irrigationType.name,
    notes = notes,
    createdAtEpochMillis = createdAt.toEpochMilliseconds(),
)
