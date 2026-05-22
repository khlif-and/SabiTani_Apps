package tech.sabitani.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import tech.sabitani.core.navigation.SabiTaniNavHost
import tech.sabitani.feature.cycle.presentation.screen.CycleDetailRoute
import tech.sabitani.feature.cycle.presentation.screen.CycleFormRoute
import tech.sabitani.feature.cycle.presentation.screen.cycleDetailScreen
import tech.sabitani.feature.cycle.presentation.screen.cycleFormScreen
import tech.sabitani.feature.home.presentation.screen.HomeRoute
import tech.sabitani.feature.home.presentation.screen.homeScreen
import tech.sabitani.feature.plot.presentation.screen.FarmListRoute
import tech.sabitani.feature.plot.presentation.screen.PlotDetailRoute
import tech.sabitani.feature.plot.presentation.screen.PlotListRoute
import tech.sabitani.feature.plot.presentation.screen.farmListScreen
import tech.sabitani.feature.plot.presentation.screen.plotDetailScreen
import tech.sabitani.feature.plot.presentation.screen.plotListScreen

@Composable
internal fun SabiTaniMainShell(modifier: Modifier = Modifier) {
    val mainNavController = rememberNavController()
    val currentBackStack by mainNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            MainShellBottomBar(
                currentDestination = currentDestination,
                onTabSelected = { destination ->
                    mainNavController.navigateToTopLevel(destination)
                },
            )
        },
    ) { padding ->
        SabiTaniNavHost(
            navController = mainNavController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(padding),
        ) {
            homeScreen(
                onNavigateToFarms = { mainNavController.navigateToTopLevel(TopLevelDestination.KEBUN) },
                onNavigateToCycles = { mainNavController.navigateToTopLevel(TopLevelDestination.KEBUN) },
            )
            farmListScreen(
                onFarmClicked = { farmId, farmName ->
                    mainNavController.navigate(PlotListRoute(farmId = farmId, farmName = farmName))
                },
            )
            plotListScreen(
                onBack = { mainNavController.popBackStack() },
                onPlotClicked = { plotId ->
                    mainNavController.navigate(PlotDetailRoute(plotId = plotId))
                },
            )
            plotDetailScreen(
                onBack = { mainNavController.popBackStack() },
                onStartCycle = { plotId ->
                    mainNavController.navigate(CycleFormRoute(plotId = plotId))
                },
            )
            cycleFormScreen(
                onBack = { mainNavController.popBackStack() },
                onSubmitted = { cycleId ->
                    mainNavController.navigate(CycleDetailRoute(cycleId = cycleId)) {
                        popUpTo<CycleFormRoute> { inclusive = true }
                    }
                },
            )
            cycleDetailScreen(onBack = { mainNavController.popBackStack() })
            taniaPlaceholderScreen()
            profilePlaceholderScreen()
        }
    }
}

@Composable
private fun MainShellBottomBar(
    currentDestination: NavDestination?,
    onTabSelected: (TopLevelDestination) -> Unit,
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = destination.matches(currentDestination)
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(destination) },
                icon = { Icon(imageVector = destination.icon, contentDescription = null) },
                label = { Text(destination.label) },
                alwaysShowLabel = true,
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    val topLevelOptions =
        navOptions {
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    val route: Any =
        when (destination) {
            TopLevelDestination.HOME -> HomeRoute
            TopLevelDestination.KEBUN -> FarmListRoute
            TopLevelDestination.TANIA -> TaniaPlaceholderRoute
            TopLevelDestination.PROFIL -> ProfilePlaceholderRoute
        }
    navigate(route = route, navOptions = topLevelOptions)
}
