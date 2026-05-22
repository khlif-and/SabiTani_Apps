package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.datetime.LocalDate
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository
import javax.inject.Inject

class StartCycleUseCase
    @Inject
    constructor(
        private val cycleRepository: CropCycleRepository,
    ) {
        suspend operator fun invoke(
            plotId: Long,
            commodity: String,
            variety: String?,
            startDate: LocalDate,
            targetHarvestDate: LocalDate?,
            notes: String?,
        ): Result<Long> =
            runCatching {
                require(plotId > 0L) { "Siklus harus terkait petak." }
                require(commodity.isNotBlank()) { "Nama komoditas tidak boleh kosong." }
                targetHarvestDate?.let {
                    require(it >= startDate) { "Target panen tidak boleh sebelum tanggal tanam." }
                }
                cycleRepository.addCycle(
                    plotId = plotId,
                    commodity = commodity.trim(),
                    variety = variety?.trim()?.takeIf(String::isNotEmpty),
                    startDate = startDate,
                    targetHarvestDate = targetHarvestDate,
                    notes = notes?.trim()?.takeIf(String::isNotEmpty),
                )
            }
    }
