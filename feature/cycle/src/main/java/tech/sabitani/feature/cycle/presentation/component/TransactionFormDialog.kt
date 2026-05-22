package tech.sabitani.feature.cycle.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tech.sabitani.core.model.TransactionCategory
import tech.sabitani.feature.cycle.presentation.state.CycleDetailIntent
import tech.sabitani.feature.cycle.presentation.state.TransactionDraft
import tech.sabitani.feature.cycle.presentation.state.TransactionIntent

@Composable
internal fun TransactionFormDialog(
    draft: TransactionDraft,
    onIntent: (CycleDetailIntent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onIntent(TransactionIntent.DismissTransactionDialog) },
        confirmButton = {
            TextButton(
                onClick = { onIntent(TransactionIntent.SubmitTransaction) },
                enabled = !draft.isSubmitting,
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(
                onClick = { onIntent(TransactionIntent.DismissTransactionDialog) },
                enabled = !draft.isSubmitting,
            ) { Text("Batal") }
        },
        title = { Text("Catat Transaksi") },
        text = { TransactionFormFields(draft = draft, onIntent = onIntent) },
    )
}

@Composable
private fun TransactionFormFields(
    draft: TransactionDraft,
    onIntent: (CycleDetailIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnumDropdown(
            label = "Kategori",
            options = TransactionCategory.entries,
            selected = draft.category,
            displayName = { "${it.type.displayName} · ${it.displayName}" },
            onSelected = { onIntent(TransactionIntent.TransactionCategoryChanged(it)) },
        )
        OutlinedTextField(
            value = draft.amountText,
            onValueChange = { onIntent(TransactionIntent.TransactionAmountChanged(it)) },
            label = { Text("Nominal (Rp)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        DatePickerField(
            label = "Tanggal",
            value = draft.occurredOn,
            onValueChange = { date ->
                date?.let { onIntent(TransactionIntent.TransactionDateChanged(it)) }
            },
        )
        OutlinedTextField(
            value = draft.notes,
            onValueChange = { onIntent(TransactionIntent.TransactionNotesChanged(it)) },
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
