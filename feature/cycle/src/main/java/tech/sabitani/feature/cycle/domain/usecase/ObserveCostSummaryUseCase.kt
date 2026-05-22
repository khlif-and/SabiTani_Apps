package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository
import javax.inject.Inject

class ObserveCostSummaryUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
    ) {
        operator fun invoke(cycleId: Long): Flow<CycleCostSummary> = transactionRepository.observeCostSummary(cycleId)
    }
