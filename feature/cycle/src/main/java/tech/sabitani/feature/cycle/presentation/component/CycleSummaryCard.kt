package tech.sabitani.feature.cycle.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.sabitani.core.model.CropCycle
import tech.sabitani.core.model.CycleCostSummary

@Composable
internal fun CycleSummaryCard(
    cycle: CropCycle,
    costSummary: CycleCostSummary?,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(cycle.commodity, style = MaterialTheme.typography.titleLarge)
            cycle.variety?.takeIf(String::isNotBlank)?.let {
                Text("Varietas: $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text =
                    "Tanam: ${cycle.startDate}" +
                        (cycle.targetHarvestDate?.let { " · Target panen: $it" } ?: ""),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Status: ${cycle.status.displayName}",
                style = MaterialTheme.typography.bodyMedium,
            )
            costSummary?.let { CostSummaryRow(it) }
        }
    }
}

@Composable
private fun CostSummaryRow(summary: CycleCostSummary) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        SummaryCell(label = "Pemasukan", amount = summary.totalIncomeIdr)
        SummaryCell(label = "Pengeluaran", amount = summary.totalExpenseIdr)
        SummaryCell(
            label = if (summary.profitLossIdr >= 0) "Laba" else "Rugi",
            amount = summary.profitLossIdr,
            isProfit = summary.profitLossIdr >= 0,
        )
    }
}

@Composable
private fun SummaryCell(
    label: String,
    amount: Long,
    isProfit: Boolean = true,
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = "Rp ${"%,d".format(amount)}",
            style = MaterialTheme.typography.titleMedium,
            color = if (isProfit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
        )
    }
}
