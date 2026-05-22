package tech.sabitani.feature.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
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
import tech.sabitani.core.model.DashboardSummary
import tech.sabitani.core.ui.state.ErrorState
import tech.sabitani.core.ui.state.LoadingState
import tech.sabitani.feature.home.presentation.component.DashboardCard
import tech.sabitani.feature.home.presentation.state.HomeEffect
import tech.sabitani.feature.home.presentation.state.HomeIntent
import tech.sabitani.feature.home.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    onNavigateToFarms: () -> Unit,
    onNavigateToCycles: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            HomeEffect.NavigateToFarms -> onNavigateToFarms()
            HomeEffect.NavigateToCycles -> onNavigateToCycles()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Beranda") }) },
    ) { padding ->
        when {
            state.isLoading && state.summary == null -> {
                LoadingState(modifier = Modifier.padding(padding))
            }
            state.errorMessage != null -> {
                ErrorState(
                    modifier = Modifier.padding(padding),
                    message = state.errorMessage ?: "Terjadi kesalahan.",
                    onRetry = { viewModel.onIntent(HomeIntent.Refresh) },
                )
            }
            else -> {
                DashboardContent(
                    summary = state.summary ?: DashboardSummary(0, 0, 0, 0, 0),
                    contentPadding = padding,
                    onOpenFarms = { viewModel.onIntent(HomeIntent.OpenFarms) },
                    onOpenCycles = { viewModel.onIntent(HomeIntent.OpenCycles) },
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    summary: DashboardSummary,
    contentPadding: PaddingValues,
    onOpenFarms: () -> Unit,
    onOpenCycles: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Ringkasan kebun Anda",
            style = MaterialTheme.typography.titleMedium,
        )

        DashboardCard(
            label = "Total kebun",
            value = summary.farmCount.toString(),
            icon = Icons.Outlined.Agriculture,
            accent = MaterialTheme.colorScheme.primary,
            supporting = "Kelola seluruh kebun yang Anda catat",
            onClick = onOpenFarms,
        )

        DashboardCard(
            label = "Plot terdata",
            value = summary.plotCount.toString(),
            icon = Icons.Outlined.Map,
            accent = MaterialTheme.colorScheme.secondary,
            onClick = onOpenFarms,
        )

        DashboardCard(
            label = "Siklus tanam berjalan",
            value = summary.activeCycleCount.toString(),
            icon = Icons.Outlined.Grass,
            accent = MaterialTheme.colorScheme.tertiary,
            supporting = "Klik untuk lihat detail siklus aktif",
            onClick = onOpenCycles,
        )

        val netLabel =
            if (summary.monthlyNetIdr >= 0) {
                "Laba bulan ini"
            } else {
                "Rugi bulan ini"
            }
        DashboardCard(
            label = netLabel,
            value = formatRupiah(summary.monthlyNetIdr),
            icon = Icons.Outlined.TrendingUp,
            accent =
                if (summary.monthlyNetIdr >= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            supporting =
                "Pemasukan ${formatRupiah(summary.monthlyIncomeIdr)} · " +
                    "Pengeluaran ${formatRupiah(summary.monthlyExpenseIdr)}",
        )
    }
}

private fun formatRupiah(amount: Long): String {
    val absText = "%,d".format(kotlin.math.abs(amount)).replace(',', '.')
    val prefix = if (amount < 0) "-Rp " else "Rp "
    return prefix + absText
}

fun NavGraphBuilder.homeScreen(
    onNavigateToFarms: () -> Unit,
    onNavigateToCycles: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onNavigateToFarms = onNavigateToFarms,
            onNavigateToCycles = onNavigateToCycles,
        )
    }
}
