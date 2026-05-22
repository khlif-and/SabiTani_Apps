package tech.sabitani.feature.cycle.domain.usecase

import kotlinx.datetime.LocalDate
import tech.sabitani.core.model.TransactionCategory
import tech.sabitani.feature.cycle.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase
    @Inject
    constructor(
        private val transactionRepository: TransactionRepository,
    ) {
        suspend operator fun invoke(
            cycleId: Long,
            category: TransactionCategory,
            amountIdr: Long,
            occurredOn: LocalDate,
            notes: String?,
        ): Result<Long> =
            runCatching {
                require(cycleId > 0L) { "Transaksi harus terkait siklus tanam." }
                require(amountIdr > 0L) { "Nominal harus lebih dari 0." }
                transactionRepository.addTransaction(
                    cycleId = cycleId,
                    category = category,
                    amountIdr = amountIdr,
                    occurredOn = occurredOn,
                    notes = notes?.trim()?.takeIf(String::isNotEmpty),
                )
            }
    }
