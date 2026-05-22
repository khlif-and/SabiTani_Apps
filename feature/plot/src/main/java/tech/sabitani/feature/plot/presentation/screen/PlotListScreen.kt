package tech.sabitani.feature.plot.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import tech.sabitani.core.model.Plot
import tech.sabitani.feature.plot.presentation.component.AddPlotDialog
import tech.sabitani.feature.plot.presentation.component.FarmListEmptyState
import tech.sabitani.feature.plot.presentation.state.PlotListEffect
import tech.sabitani.feature.plot.presentation.state.PlotListIntent
import tech.sabitani.feature.plot.presentation.viewmodel.PlotListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlotListScreen(
    onBack: () -> Unit,
    onPlotClicked: (plotId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlotListViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is PlotListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            is PlotListEffect.NavigateToPlotDetail -> onPlotClicked(effect.plotId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.farmName.ifBlank { "Petak" }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onIntent(PlotListIntent.OpenAddDialog) }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah petak")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.plots.isEmpty() && !state.isLoading) {
            FarmListEmptyState(
                title = "Belum ada petak",
                description = "Tambah petak/bedeng pertama untuk mulai siklus tanam.",
                modifier = Modifier.padding(padding),
            )
        } else {
            PlotList(
                plots = state.plots,
                contentPadding = padding,
                onClick = { viewModel.onIntent(PlotListIntent.PlotClicked(it.id)) },
            )
        }
    }

    if (state.isAddDialogVisible) {
        AddPlotDialog(
            name = state.draftName,
            areaText = state.draftAreaText,
            soilType = state.draftSoilType,
            irrigationType = state.draftIrrigationType,
            notes = state.draftNotes,
            isSubmitting = state.isSubmitting,
            onNameChange = { viewModel.onIntent(PlotListIntent.NameChanged(it)) },
            onAreaChange = { viewModel.onIntent(PlotListIntent.AreaChanged(it)) },
            onSoilTypeChange = { viewModel.onIntent(PlotListIntent.SoilTypeChanged(it)) },
            onIrrigationTypeChange = { viewModel.onIntent(PlotListIntent.IrrigationTypeChanged(it)) },
            onNotesChange = { viewModel.onIntent(PlotListIntent.NotesChanged(it)) },
            onConfirm = { viewModel.onIntent(PlotListIntent.SubmitAddPlot) },
            onDismiss = { viewModel.onIntent(PlotListIntent.DismissAddDialog) },
        )
    }
}

@Composable
private fun PlotList(
    plots: List<Plot>,
    contentPadding: PaddingValues,
    onClick: (Plot) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 88.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = plots, key = Plot::id) { plot ->
            Card(onClick = { onClick(plot) }, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(plot.name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${"%,.0f".format(plot.areaSqM)} m² · ${plot.soilType.displayName} · ${plot.irrigationType.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

fun NavGraphBuilder.plotListScreen(
    onBack: () -> Unit,
    onPlotClicked: (plotId: Long) -> Unit,
) {
    composable<PlotListRoute> {
        PlotListScreen(onBack = onBack, onPlotClicked = onPlotClicked)
    }
}
