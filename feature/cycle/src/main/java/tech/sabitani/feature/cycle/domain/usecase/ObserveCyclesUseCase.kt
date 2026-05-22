package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.CropCycle
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository
import javax.inject.Inject

class ObserveCyclesUseCase
    @Inject
    constructor(
        private val cycleRepository: CropCycleRepository,
    ) {
        operator fun invoke(plotId: Long): Flow<List<CropCycle>> = cycleRepository.observeCyclesByPlot(plotId)
    }
