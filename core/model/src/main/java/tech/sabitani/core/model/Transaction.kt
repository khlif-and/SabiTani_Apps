package tech.sabitani.core.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

data class Transaction(
    val id: Long = 0L,
    val cycleId: Long,
    val type: TransactionType,
    val category: TransactionCategory,
    val amountIdr: Long,
    val occurredOn: LocalDate,
    val notes: String?,
    val createdAt: Instant,
)

enum class TransactionType(val displayName: String) {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran"),
}

enum class TransactionCategory(val displayName: String, val type: TransactionType) {
    SEED("Bibit", TransactionType.EXPENSE),
    FERTILIZER("Pupuk", TransactionType.EXPENSE),
    PESTICIDE("Pestisida", TransactionType.EXPENSE),
    LABOR("Tenaga Kerja", TransactionType.EXPENSE),
    EQUIPMENT("Peralatan", TransactionType.EXPENSE),
    IRRIGATION_FUEL("Bahan Bakar Irigasi", TransactionType.EXPENSE),
    OTHER_EXPENSE("Pengeluaran Lain", TransactionType.EXPENSE),
    SALE("Hasil Panen", TransactionType.INCOME),
    OTHER_INCOME("Pemasukan Lain", TransactionType.INCOME),
}
