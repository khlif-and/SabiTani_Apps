package tech.sabitani.feature.cycle.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tech.sabitani.core.model.ActivityType
import tech.sabitani.feature.cycle.presentation.state.ActivityDraft

@Composable
internal fun ActivityFormDialog(
    draft: ActivityDraft,
    onTypeChange: (ActivityType) -> Unit,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit,
    onMaterialChange: (String) -> Unit,
    onDosageChange: (String) -> Unit,
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
        title = { Text("Catat Aktivitas") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                EnumDropdown(
                    label = "Jenis aktivitas",
                    options = ActivityType.entries,
                    selected = draft.type,
                    displayName = ActivityType::displayName,
                    onSelected = onTypeChange,
                )
                DatePickerField(
                    label = "Tanggal",
                    value = draft.performedOn,
                    onValueChange = { it?.let(onDateChange) },
                )
                OutlinedTextField(
                    value = draft.material,
                    onValueChange = onMaterialChange,
                    label = { Text("Material (cth: Urea, Decis)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = draft.dosage,
                    onValueChange = onDosageChange,
                    label = { Text("Dosis (cth: 200 ml/ha)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = draft.notes,
                    onValueChange = onNotesChange,
                    label = { Text("Catatan") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
