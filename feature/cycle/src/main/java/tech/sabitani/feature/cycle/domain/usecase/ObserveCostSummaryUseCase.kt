package tech.sabitani.feature.cycle.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository

class ObserveCostSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(cycleId: Long): Flow<CycleCostSummary> =
        transactionRepository.observeCostSummary(cycleId)
}
