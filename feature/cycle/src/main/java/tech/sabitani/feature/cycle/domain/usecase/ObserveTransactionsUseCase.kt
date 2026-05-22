package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.core.model.Transaction
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository
import javax.inject.Inject

class ObserveTransactionsUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
    ) {
        operator fun invoke(cycleId: Long): Flow<List<Transaction>> = transactionRepository.observeTransactions(cycleId)
    }
