package tech.sabitani.feature.plot.domain.usecase

import tech.sabitani.feature.plot.domain.repository.FarmRepository
import javax.inject.Inject

class AddFarmUseCase
    @Inject
    constructor(
        private val farmRepository: FarmRepository,
    ) {
        suspend operator fun invoke(
            name: String,
            location: String?,
            totalAreaSqM: Double?,
        ): Result<Long> =
            runCatching {
                require(name.isNotBlank()) { "Nama kebun tidak boleh kosong." }
                farmRepository.addFarm(
                    name = name.trim(),
                    location = location?.trim()?.takeIf(String::isNotEmpty),
                    totalAreaSqM = totalAreaSqM,
                )
            }
    }
