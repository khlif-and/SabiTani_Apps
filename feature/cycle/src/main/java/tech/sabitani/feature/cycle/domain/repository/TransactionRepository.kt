package tech.sabitani.feature.cycle.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.core.model.Transaction
import tech.sabitani.core.model.TransactionCategory

interface TransactionRepository {
    fun observeTransactions(cycleId: Long): Flow<List<Transaction>>

    fun observeCostSummary(cycleId: Long): Flow<CycleCostSummary>

    suspend fun addTransaction(
        cycleId: Long,
        category: TransactionCategory,
        amountIdr: Long,
        occurredOn: LocalDate,
        notes: String?,
    ): Long

    suspend fun deleteTransaction(id: Long)
}
