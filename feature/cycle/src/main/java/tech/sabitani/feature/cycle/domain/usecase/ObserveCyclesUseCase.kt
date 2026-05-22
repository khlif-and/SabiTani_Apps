package tech.sabitani.feature.cycle.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.CropCycle
import tech.sabitani.feature.cycle.domain.repository.CropCycleRepository

class ObserveCyclesUseCase @Inject constructor(
    private val cycleRepository: CropCycleRepository,
) {
    operator fun invoke(plotId: Long): Flow<List<CropCycle>> =
        cycleRepository.observeCyclesByPlot(plotId)
}
