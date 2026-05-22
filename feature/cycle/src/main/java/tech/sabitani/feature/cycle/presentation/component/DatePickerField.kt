package tech.sabitani.feature.cycle.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DatePickerField(
    label: String,
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    allowClear: Boolean = false,
) {
    var open by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value?.toString().orEmpty(),
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { open = true },
        )
    }
    if (open) {
        val state =
            rememberDatePickerState(
                initialSelectedDateMillis = value?.toUtcStartOfDayMillis(),
            )
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        onValueChange(Instant.fromEpochMilliseconds(millis).toUtcLocalDate())
                    }
                    open = false
                }) { Text("Pilih") }
            },
            dismissButton = {
                if (allowClear && value != null) {
                    TextButton(onClick = {
                        onValueChange(null)
                        open = false
                    }) { Text("Hapus") }
                } else {
                    TextButton(onClick = { open = false }) { Text("Batal") }
                }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

private fun LocalDate.toUtcStartOfDayMillis(): Long = LocalDateTime(this, LocalTime(0, 0)).toInstant(TimeZone.UTC).toEpochMilliseconds()

private fun Instant.toUtcLocalDate(): LocalDate = toLocalDateTime(TimeZone.UTC).date
