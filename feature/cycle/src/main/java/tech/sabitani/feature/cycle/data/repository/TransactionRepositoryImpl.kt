package tech.sabitani.feature.cycle.data.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.dao.TransactionDao
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.core.model.Transaction
import tech.sabitani.core.model.TransactionCategory
import tech.sabitani.feature.cycle.data.mapper.newTransactionEntity
import tech.sabitani.feature.cycle.data.mapper.toDomain
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository

internal class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val clock: Clock,
) : TransactionRepository {

    override fun observeTransactions(cycleId: Long): Flow<List<Transaction>> =
        transactionDao.observeByCycle(cycleId).map { list -> list.map { it.toDomain() } }

    override fun observeCostSummary(cycleId: Long): Flow<CycleCostSummary> =
        transactionDao.observeTotalsByCycle(cycleId).map { it.toDomain() }

    override suspend fun addTransaction(
        cycleId: Long,
        category: TransactionCategory,
        amountIdr: Long,
        occurredOn: LocalDate,
        notes: String?,
    ): Long = transactionDao.insert(
        newTransactionEntity(
            cycleId = cycleId,
            category = category,
            amountIdr = amountIdr,
            occurredOn = occurredOn,
            notes = notes,
            createdAt = clock.now(),
        ),
    )

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteById(id)
    }
}
