package tech.sabitani.feature.plot.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Plot
import tech.sabitani.feature.plot.domain.repository.PlotRepository

class ObservePlotsUseCase @Inject constructor(
    private val plotRepository: PlotRepository,
) {
    operator fun invoke(farmId: Long): Flow<List<Plot>> = plotRepository.observePlots(farmId)
}
