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
import tech.sabitani.feature.cycle.presentation.state.ActivityIntent
import tech.sabitani.feature.cycle.presentation.state.CycleDetailIntent

@Composable
internal fun ActivityFormDialog(
    draft: ActivityDraft,
    onIntent: (CycleDetailIntent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onIntent(ActivityIntent.DismissActivityDialog) },
        confirmButton = {
            TextButton(
                onClick = { onIntent(ActivityIntent.SubmitActivity) },
                enabled = !draft.isSubmitting,
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(
                onClick = { onIntent(ActivityIntent.DismissActivityDialog) },
                enabled = !draft.isSubmitting,
            ) { Text("Batal") }
        },
        title = { Text("Catat Aktivitas") },
        text = { ActivityFormFields(draft = draft, onIntent = onIntent) },
    )
}

@Composable
private fun ActivityFormFields(
    draft: ActivityDraft,
    onIntent: (CycleDetailIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        EnumDropdown(
            label = "Jenis aktivitas",
            options = ActivityType.entries,
            selected = draft.type,
            displayName = ActivityType::displayName,
            onSelected = { onIntent(ActivityIntent.ActivityTypeChanged(it)) },
        )
        DatePickerField(
            label = "Tanggal",
            value = draft.performedOn,
            onValueChange = { date ->
                date?.let { onIntent(ActivityIntent.ActivityDateChanged(it)) }
            },
        )
        OutlinedTextField(
            value = draft.material,
            onValueChange = { onIntent(ActivityIntent.ActivityMaterialChanged(it)) },
            label = { Text("Material (cth: Urea, Decis)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = draft.dosage,
            onValueChange = { onIntent(ActivityIntent.ActivityDosageChanged(it)) },
            label = { Text("Dosis (cth: 200 ml/ha)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = draft.notes,
            onValueChange = { onIntent(ActivityIntent.ActivityNotesChanged(it)) },
            label = { Text("Catatan") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
