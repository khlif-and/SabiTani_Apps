package tech.sabitani.feature.plot.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import tech.sabitani.core.model.Farm
import tech.sabitani.feature.plot.presentation.component.AddFarmDialog
import tech.sabitani.feature.plot.presentation.component.FarmListEmptyState
import tech.sabitani.feature.plot.presentation.state.FarmListEffect
import tech.sabitani.feature.plot.presentation.state.FarmListIntent
import tech.sabitani.feature.plot.presentation.viewmodel.FarmListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FarmListScreen(
    onFarmClicked: (farmId: Long, farmName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FarmListViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is FarmListEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            is FarmListEffect.NavigateToPlotList -> onFarmClicked(effect.farmId, effect.farmName)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Kebun Saya") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onIntent(FarmListIntent.OpenAddDialog) }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah kebun")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.farms.isEmpty() && !state.isLoading) {
            FarmListEmptyState(
                title = "Belum ada kebun",
                description = "Tambah kebun pertama Anda untuk mulai mencatat petak dan siklus tanam.",
                modifier = Modifier.padding(padding),
            )
        } else {
            FarmList(
                farms = state.farms,
                contentPadding = padding,
                onFarmClick = { viewModel.onIntent(FarmListIntent.FarmClicked(it.id)) },
            )
        }
    }

    if (state.isAddDialogVisible) {
        AddFarmDialog(
            name = state.draftName,
            location = state.draftLocation,
            totalAreaText = state.draftTotalAreaText,
            isSubmitting = state.isSubmitting,
            onNameChange = { viewModel.onIntent(FarmListIntent.NameChanged(it)) },
            onLocationChange = { viewModel.onIntent(FarmListIntent.LocationChanged(it)) },
            onAreaChange = { viewModel.onIntent(FarmListIntent.TotalAreaChanged(it)) },
            onConfirm = { viewModel.onIntent(FarmListIntent.SubmitAddFarm) },
            onDismiss = { viewModel.onIntent(FarmListIntent.DismissAddDialog) },
        )
    }
}

@Composable
private fun FarmList(
    farms: List<Farm>,
    contentPadding: PaddingValues,
    onFarmClick: (Farm) -> Unit,
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
        items(items = farms, key = Farm::id) { farm ->
            Card(
                onClick = { onFarmClick(farm) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                FarmCardContent(farm)
            }
        }
    }
}

@Composable
private fun FarmCardContent(farm: Farm) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(farm.name, style = MaterialTheme.typography.titleMedium)
        farm.location?.takeIf(String::isNotBlank)?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        farm.totalAreaSqM?.let {
            Text(
                text = "Total: ${"%,.0f".format(it)} m²",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

fun NavGraphBuilder.farmListScreen(onFarmClicked: (farmId: Long, farmName: String) -> Unit) {
    composable<FarmListRoute> {
        FarmListScreen(onFarmClicked = onFarmClicked)
    }
}
