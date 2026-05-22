package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.CropCycle
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository
import javax.inject.Inject

class ObserveCycleDetailUseCase
    @Inject
    constructor(
        private val cycleRepository: CropCycleRepository,
    ) {
        operator fun invoke(cycleId: Long): Flow<CropCycle?> = cycleRepository.observeCycle(cycleId)
    }
