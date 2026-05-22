package tech.sabitani.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import tech.sabitani.core.navigation.SabiTaniNavHost
import tech.sabitani.feature.auth.presentation.screen.LoginRoute
import tech.sabitani.feature.auth.presentation.screen.loginScreen
import tech.sabitani.feature.onboarding.presentation.screen.OnboardingRoute
import tech.sabitani.feature.onboarding.presentation.screen.onboardingScreen
import tech.sabitani.feature.splash.presentation.screen.SplashRoute
import tech.sabitani.feature.splash.presentation.screen.splashScreen

@Composable
fun SabiTaniAppNavGraph(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
) {
    SabiTaniNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        splashScreen(
            onTimeout = {
                navController.navigate(
                    route = OnboardingRoute,
                    navOptions = navOptions { popUpTo(SplashRoute) { inclusive = true } },
                )
            },
        )
        onboardingScreen(
            onFinished = {
                navController.navigate(
                    route = LoginRoute,
                    navOptions = navOptions { popUpTo(OnboardingRoute) { inclusive = true } },
                )
            },
        )
        loginScreen(
            onLoginSuccess = {
                // TODO: navigate to :feature:home saat feature itu sudah ada (Phase berikutnya).
            },
        )
    }
}
