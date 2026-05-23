package tech.sabitani.feature.tania.presentation.screen

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object ChatRoute

fun NavGraphBuilder.chatScreen() {
    composable<ChatRoute> {
        ChatScreen()
    }
}
