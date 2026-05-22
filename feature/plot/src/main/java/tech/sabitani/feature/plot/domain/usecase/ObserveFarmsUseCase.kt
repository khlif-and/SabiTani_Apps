package tech.sabitani.feature.plot.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Farm
import tech.sabitani.feature.plot.domain.repository.FarmRepository
import javax.inject.Inject

class ObserveFarmsUseCase
    @Inject
    constructor(
        private val farmRepository: FarmRepository,
    ) {
        operator fun invoke(): Flow<List<Farm>> = farmRepository.observeFarms()
    }
