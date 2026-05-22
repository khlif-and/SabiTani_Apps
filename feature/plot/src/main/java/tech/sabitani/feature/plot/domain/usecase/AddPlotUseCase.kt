package tech.sabitani.feature.plot.domain.usecase

import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.SoilType
import tech.sabitani.feature.plot.domain.repository.PlotRepository
import javax.inject.Inject

class AddPlotUseCase
    @Inject
    constructor(
        private val plotRepository: PlotRepository,
    ) {
        suspend operator fun invoke(
            farmId: Long,
            name: String,
            areaSqM: Double,
            soilType: SoilType,
            irrigationType: IrrigationType,
            notes: String?,
        ): Result<Long> =
            runCatching {
                require(name.isNotBlank()) { "Nama petak tidak boleh kosong." }
                require(areaSqM > 0.0) { "Luas petak harus lebih dari 0 m²." }
                require(farmId > 0L) { "Petak harus terhubung ke kebun." }
                plotRepository.addPlot(
                    farmId = farmId,
                    name = name.trim(),
                    areaSqM = areaSqM,
                    soilType = soilType,
                    irrigationType = irrigationType,
                    notes = notes?.trim()?.takeIf(String::isNotEmpty),
                )
            }
    }
