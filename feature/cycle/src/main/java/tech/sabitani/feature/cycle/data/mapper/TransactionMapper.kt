package tech.sabitani.feature.cycle.data.mapper

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import tech.sabitani.core.database.entity.TransactionEntity
import tech.sabitani.core.database.projection.CycleTransactionTotals
import tech.sabitani.core.model.CycleCostSummary
import tech.sabitani.core.model.Transaction
import tech.sabitani.core.model.TransactionCategory
import tech.sabitani.core.model.TransactionType

internal fun TransactionEntity.toDomain(): Transaction =
    Transaction(
        id = id,
        cycleId = cycleId,
        type = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.EXPENSE),
        category =
            runCatching { TransactionCategory.valueOf(category) }
                .getOrDefault(TransactionCategory.OTHER_EXPENSE),
        amountIdr = amountIdr,
        occurredOn = LocalDate.parse(occurredOnIso),
        notes = notes,
        createdAt = Instant.fromEpochMilliseconds(createdAtEpochMillis),
    )

internal fun newTransactionEntity(
    cycleId: Long,
    category: TransactionCategory,
    amountIdr: Long,
    occurredOn: LocalDate,
    notes: String?,
    createdAt: Instant,
): TransactionEntity =
    TransactionEntity(
        cycleId = cycleId,
        type = category.type.name,
        category = category.name,
        amountIdr = amountIdr,
        occurredOnIso = occurredOn.toString(),
        notes = notes,
        createdAtEpochMillis = createdAt.toEpochMilliseconds(),
    )

internal fun CycleTransactionTotals.toDomain(): CycleCostSummary =
    CycleCostSummary(
        cycleId = cycleId,
        totalIncomeIdr = totalIncomeIdr,
        totalExpenseIdr = totalExpenseIdr,
        transactionCount = transactionCount,
    )
