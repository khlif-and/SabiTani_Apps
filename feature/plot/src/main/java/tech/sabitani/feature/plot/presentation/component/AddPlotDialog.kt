package tech.sabitani.feature.plot.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import tech.sabitani.core.model.IrrigationType
import tech.sabitani.core.model.SoilType
import tech.sabitani.feature.plot.presentation.state.PlotListIntent
import tech.sabitani.feature.plot.presentation.state.PlotListState

@Composable
internal fun AddPlotDialog(
    state: PlotListState,
    onIntent: (PlotListIntent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onIntent(PlotListIntent.DismissAddDialog) },
        confirmButton = {
            TextButton(
                onClick = { onIntent(PlotListIntent.SubmitAddPlot) },
                enabled = !state.isSubmitting &&
                    state.draftName.isNotBlank() &&
                    state.draftAreaText.isNotBlank(),
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(
                onClick = { onIntent(PlotListIntent.DismissAddDialog) },
                enabled = !state.isSubmitting,
            ) { Text("Batal") }
        },
        title = { Text("Tambah Petak") },
        text = { AddPlotFormFields(state = state, onIntent = onIntent) },
    )
}

@Composable
private fun AddPlotFormFields(
    state: PlotListState,
    onIntent: (PlotListIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = state.draftName,
            onValueChange = { onIntent(PlotListIntent.NameChanged(it)) },
            label = { Text("Nama petak") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.draftAreaText,
            onValueChange = { onIntent(PlotListIntent.AreaChanged(it)) },
            label = { Text("Luas (m²)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
        )
        EnumDropdown(
            label = "Jenis tanah",
            options = SoilType.entries,
            selected = state.draftSoilType,
            displayName = SoilType::displayName,
            onSelected = { onIntent(PlotListIntent.SoilTypeChanged(it)) },
        )
        EnumDropdown(
            label = "Irigasi",
            options = IrrigationType.entries,
            selected = state.draftIrrigationType,
            displayName = IrrigationType::displayName,
            onSelected = { onIntent(PlotListIntent.IrrigationTypeChanged(it)) },
        )
        OutlinedTextField(
            value = state.draftNotes,
            onValueChange = { onIntent(PlotListIntent.NotesChanged(it)) },
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selected: T,
    displayName: (T) -> String,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = displayName(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(displayName(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
