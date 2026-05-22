package tech.sabitani.core.model

data class CycleCostSummary(
    val cycleId: Long,
    val totalIncomeIdr: Long,
    val totalExpenseIdr: Long,
    val transactionCount: Int,
) {
    val profitLossIdr: Long get() = totalIncomeIdr - totalExpenseIdr
}
