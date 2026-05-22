package tech.sabitani.core.database.projection

data class CycleTransactionTotals(
    val cycleId: Long,
    val totalIncomeIdr: Long,
    val totalExpenseIdr: Long,
    val transactionCount: Int,
)
