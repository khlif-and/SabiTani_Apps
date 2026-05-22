package tech.sabitani.core.model

data class DashboardSummary(
    val farmCount: Int,
    val plotCount: Int,
    val activeCycleCount: Int,
    val monthlyIncomeIdr: Long,
    val monthlyExpenseIdr: Long,
) {
    val monthlyNetIdr: Long get() = monthlyIncomeIdr - monthlyExpenseIdr
}
