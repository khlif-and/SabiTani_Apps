package tech.sabitani.feature.plot.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Plot
import tech.sabitani.feature.plot.domain.repository.PlotRepository
import javax.inject.Inject

class ObservePlotDetailUseCase
    @Inject
    constructor(
        private val plotRepository: PlotRepository,
    ) {
        operator fun invoke(plotId: Long): Flow<Plot?> = plotRepository.observePlot(plotId)
    }
