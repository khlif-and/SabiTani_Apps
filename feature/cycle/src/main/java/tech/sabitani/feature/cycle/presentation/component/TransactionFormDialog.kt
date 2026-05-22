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
import tech.sabitani.feature.cycle.presentation.state.TransactionDraft

@Composable
internal fun TransactionFormDialog(
    draft: TransactionDraft,
    onCategoryChange: (TransactionCategory) -> Unit,
    onAmountChange: (String) -> Unit,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit,
    onNotesChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !draft.isSubmitting) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !draft.isSubmitting) { Text("Batal") }
        },
        title = { Text("Catat Transaksi") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EnumDropdown(
                    label = "Kategori",
                    options = TransactionCategory.entries,
                    selected = draft.category,
                    displayName = { "${it.type.displayName} · ${it.displayName}" },
                    onSelected = onCategoryChange,
                )
                OutlinedTextField(
                    value = draft.amountText,
                    onValueChange = onAmountChange,
                    label = { Text("Nominal (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                DatePickerField(
                    label = "Tanggal",
                    value = draft.occurredOn,
                    onValueChange = { it?.let(onDateChange) },
                )
                OutlinedTextField(
                    value = draft.notes,
                    onValueChange = onNotesChange,
                    label = { Text("Catatan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
