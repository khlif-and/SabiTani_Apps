package tech.sabitani.feature.plot.presentation.component

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
import tech.sabitani.feature.plot.presentation.state.FarmListIntent
import tech.sabitani.feature.plot.presentation.state.FarmListState

@Composable
internal fun AddFarmDialog(
    state: FarmListState,
    onIntent: (FarmListIntent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onIntent(FarmListIntent.DismissAddDialog) },
        confirmButton = {
            TextButton(
                onClick = { onIntent(FarmListIntent.SubmitAddFarm) },
                enabled = !state.isSubmitting && state.draftName.isNotBlank(),
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(
                onClick = { onIntent(FarmListIntent.DismissAddDialog) },
                enabled = !state.isSubmitting,
            ) { Text("Batal") }
        },
        title = { Text("Tambah Kebun") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedTextField(
                    value = state.draftName,
                    onValueChange = { onIntent(FarmListIntent.NameChanged(it)) },
                    label = { Text("Nama kebun") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = state.draftLocation,
                    onValueChange = { onIntent(FarmListIntent.LocationChanged(it)) },
                    label = { Text("Lokasi (opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = state.draftTotalAreaText,
                    onValueChange = { onIntent(FarmListIntent.TotalAreaChanged(it)) },
                    label = { Text("Total luas (m², opsional)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    )
}
