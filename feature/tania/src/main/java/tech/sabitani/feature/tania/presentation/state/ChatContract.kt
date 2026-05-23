package tech.sabitani.feature.tania.presentation.state

import tech.sabitani.feature.tania.domain.model.ChatMessage

data class ChatState(
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ChatIntent {
    data class DraftChanged(
        val value: String,
    ) : ChatIntent

    data object SendClicked : ChatIntent

    data object ErrorDismissed : ChatIntent

    data object ClearHistoryClicked : ChatIntent
}

sealed interface ChatEffect {
    data class ShowError(
        val message: String,
    ) : ChatEffect
}
