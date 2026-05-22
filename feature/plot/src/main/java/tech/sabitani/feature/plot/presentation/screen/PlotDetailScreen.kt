package tech.sabitani.feature.plot.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.orbitmvi.orbit.compose.collectSideEffect
import tech.sabitani.core.model.Plot
import tech.sabitani.feature.plot.presentation.state.PlotDetailEffect
import tech.sabitani.feature.plot.presentation.state.PlotDetailIntent
import tech.sabitani.feature.plot.presentation.viewmodel.PlotDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlotDetailScreen(
    onBack: () -> Unit,
    onStartCycle: (plotId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlotDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is PlotDetailEffect.NavigateToStartCycle -> onStartCycle(effect.plotId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(state.plot?.name ?: "Detail Petak") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        state.plot?.let { plot ->
            PlotDetailContent(
                plot = plot,
                onStartCycle = { viewModel.onIntent(PlotDetailIntent.StartCycleClicked) },
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun PlotDetailContent(
    plot: Plot,
    onStartCycle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(plot.name, style = MaterialTheme.typography.titleLarge)
                LabeledRow(label = "Luas", value = "${"%,.0f".format(plot.areaSqM)} m²")
                LabeledRow(label = "Jenis tanah", value = plot.soilType.displayName)
                LabeledRow(label = "Irigasi", value = plot.irrigationType.displayName)
                plot.notes?.takeIf(String::isNotBlank)?.let {
                    LabeledRow(label = "Catatan", value = it)
                }
            }
        }
        Button(onClick = onStartCycle, modifier = Modifier.fillMaxWidth()) {
            Text("Mulai siklus tanam")
        }
        Text(
            text = "Siklus tanam aktif akan tampil di sini setelah dibuat.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

fun NavGraphBuilder.plotDetailScreen(
    onBack: () -> Unit,
    onStartCycle: (plotId: Long) -> Unit,
) {
    composable<PlotDetailRoute> {
        PlotDetailScreen(onBack = onBack, onStartCycle = onStartCycle)
    }
}
