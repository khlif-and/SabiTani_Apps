package tech.sabitani.feature.cycle.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Transaction
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository

class ObserveTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    operator fun invoke(cycleId: Long): Flow<List<Transaction>> =
        transactionRepository.observeTransactions(cycleId)
}
