package tech.sabitani.feature.cycle.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.feature.cycle.presentation.component.DatePickerField
import tech.sabitani.feature.cycle.presentation.state.CycleFormEffect
import tech.sabitani.feature.cycle.presentation.state.CycleFormIntent
import tech.sabitani.feature.cycle.presentation.viewmodel.CycleFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CycleFormScreen(
    onBack: () -> Unit,
    onSubmitted: (cycleId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CycleFormViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is CycleFormEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            is CycleFormEffect.Submitted -> onSubmitted(effect.cycleId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mulai Siklus Tanam") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = state.commodity,
                onValueChange = { viewModel.onIntent(CycleFormIntent.CommodityChanged(it)) },
                label = { Text("Komoditas (cth: padi, cabai)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.variety,
                onValueChange = { viewModel.onIntent(CycleFormIntent.VarietyChanged(it)) },
                label = { Text("Varietas (opsional, cth: Ciherang)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            DatePickerField(
                label = "Tanggal tanam",
                value = state.startDate,
                onValueChange = { it?.let { viewModel.onIntent(CycleFormIntent.StartDateChanged(it)) } },
            )
            DatePickerField(
                label = "Target panen (opsional)",
                value = state.targetHarvestDate,
                onValueChange = { viewModel.onIntent(CycleFormIntent.TargetHarvestChanged(it)) },
                allowClear = true,
            )
            OutlinedTextField(
                value = state.notes,
                onValueChange = { viewModel.onIntent(CycleFormIntent.NotesChanged(it)) },
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { viewModel.onIntent(CycleFormIntent.Submit) },
                enabled = !state.isSubmitting && state.commodity.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Simpan")
            }
        }
    }
}

fun NavGraphBuilder.cycleFormScreen(
    onBack: () -> Unit,
    onSubmitted: (cycleId: Long) -> Unit,
) {
    composable<CycleFormRoute> {
        CycleFormScreen(onBack = onBack, onSubmitted = onSubmitted)
    }
}
