package tech.sabitani.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import tech.sabitani.feature.cycle.presentation.screen.CycleDetailRoute
import tech.sabitani.feature.cycle.presentation.screen.CycleFormRoute
import tech.sabitani.feature.home.presentation.screen.HomeRoute
import tech.sabitani.feature.lock.presentation.screen.SecuritySettingsRoute
import tech.sabitani.feature.lock.presentation.screen.SetupPinRoute
import tech.sabitani.feature.plot.presentation.screen.FarmListRoute
import tech.sabitani.feature.plot.presentation.screen.PlotDetailRoute
import tech.sabitani.feature.plot.presentation.screen.PlotListRoute
import tech.sabitani.feature.tania.presentation.screen.ChatRoute

internal enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
) {
    HOME(label = "Beranda", icon = Icons.Outlined.Home),
    KEBUN(label = "Kebun", icon = Icons.Outlined.Grass),
    TANIA(label = "Tania", icon = Icons.Outlined.AutoAwesome),
    PROFIL(label = "Profil", icon = Icons.Outlined.PersonOutline),
    ;

    fun matches(destination: NavDestination?): Boolean {
        if (destination == null) return false
        return when (this) {
            HOME -> destination.hasRoute<HomeRoute>()
            KEBUN ->
                destination.hasRoute<FarmListRoute>() ||
                    destination.hasRoute<PlotListRoute>() ||
                    destination.hasRoute<PlotDetailRoute>() ||
                    destination.hasRoute<CycleFormRoute>() ||
                    destination.hasRoute<CycleDetailRoute>()
            TANIA -> destination.hasRoute<ChatRoute>()
            PROFIL ->
                destination.hasRoute<ProfileRoute>() ||
                    destination.hasRoute<SecuritySettingsRoute>() ||
                    destination.hasRoute<SetupPinRoute>()
        }
    }
}
