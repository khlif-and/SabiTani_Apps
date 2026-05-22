package tech.sabitani.feature.cycle.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import tech.sabitani.core.model.CropCycle
import tech.sabitani.feature.cycle.presentation.component.ActivityFormDialog
import tech.sabitani.feature.cycle.presentation.component.ActivityTimeline
import tech.sabitani.feature.cycle.presentation.component.CycleSummaryCard
import tech.sabitani.feature.cycle.presentation.component.TransactionFormDialog
import tech.sabitani.feature.cycle.presentation.component.TransactionTimeline
import tech.sabitani.feature.cycle.presentation.state.ActivityIntent
import tech.sabitani.feature.cycle.presentation.state.CycleDetailEffect
import tech.sabitani.feature.cycle.presentation.state.CycleDetailIntent
import tech.sabitani.feature.cycle.presentation.state.CycleDetailState
import tech.sabitani.feature.cycle.presentation.state.CycleDetailTab
import tech.sabitani.feature.cycle.presentation.state.TransactionIntent
import tech.sabitani.feature.cycle.presentation.viewmodel.CycleDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CycleDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CycleDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is CycleDetailEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { CycleDetailTopBar(cycle = state.cycle, onBack = onBack) },
        floatingActionButton = { CycleDetailFab(tab = state.selectedTab, onIntent = viewModel::onIntent) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        state.cycle?.let { cycle ->
            CycleDetailBody(
                cycle = cycle,
                state = state,
                onIntent = viewModel::onIntent,
                padding = padding,
            )
        }
    }

    state.activityDraft?.let { draft ->
        ActivityFormDialog(draft = draft, onIntent = viewModel::onIntent)
    }
    state.transactionDraft?.let { draft ->
        TransactionFormDialog(draft = draft, onIntent = viewModel::onIntent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CycleDetailTopBar(
    cycle: CropCycle?,
    onBack: () -> Unit,
) {
    TopAppBar(
        title = { Text(cycle?.commodity ?: "Siklus Tanam") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
        },
    )
}

@Composable
private fun CycleDetailFab(
    tab: CycleDetailTab,
    onIntent: (CycleDetailIntent) -> Unit,
) {
    val action =
        when (tab) {
            CycleDetailTab.ACTIVITIES -> ActivityIntent.OpenActivityDialog
            CycleDetailTab.TRANSACTIONS -> TransactionIntent.OpenTransactionDialog
        }
    FloatingActionButton(onClick = { onIntent(action) }) {
        Icon(Icons.Default.Add, contentDescription = "Tambah")
    }
}

@Composable
private fun CycleDetailBody(
    cycle: CropCycle,
    state: CycleDetailState,
    onIntent: (CycleDetailIntent) -> Unit,
    padding: PaddingValues,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CycleSummaryCard(
            cycle = cycle,
            costSummary = state.costSummary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        TabRow(selectedTabIndex = state.selectedTab.ordinal) {
            CycleDetailTab.entries.forEach { tab ->
                Tab(
                    selected = state.selectedTab == tab,
                    onClick = { onIntent(CycleDetailIntent.TabSelected(tab)) },
                    text = { Text(tab.label) },
                )
            }
        }
        val listPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        when (state.selectedTab) {
            CycleDetailTab.ACTIVITIES -> ActivityTimeline(state.activities, listPadding)
            CycleDetailTab.TRANSACTIONS -> TransactionTimeline(state.transactions, listPadding)
        }
    }
}

private val CycleDetailTab.label: String
    get() =
        when (this) {
            CycleDetailTab.ACTIVITIES -> "Aktivitas"
            CycleDetailTab.TRANSACTIONS -> "Transaksi"
        }

fun NavGraphBuilder.cycleDetailScreen(onBack: () -> Unit) {
    composable<CycleDetailRoute> {
        CycleDetailScreen(onBack = onBack)
    }
}
