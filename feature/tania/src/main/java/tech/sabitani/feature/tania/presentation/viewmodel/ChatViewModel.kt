package tech.sabitani.feature.tania.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import tech.sabitani.feature.tania.domain.usecase.ClearChatHistoryUseCase
import tech.sabitani.feature.tania.domain.usecase.ObserveChatMessagesUseCase
import tech.sabitani.feature.tania.domain.usecase.SendChatMessageUseCase
import tech.sabitani.feature.tania.presentation.state.ChatEffect
import tech.sabitani.feature.tania.presentation.state.ChatIntent
import tech.sabitani.feature.tania.presentation.state.ChatState
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
    @Inject
    constructor(
        private val sendChatMessageUseCase: SendChatMessageUseCase,
        private val observeChatMessagesUseCase: ObserveChatMessagesUseCase,
        private val clearChatHistoryUseCase: ClearChatHistoryUseCase,
    ) : ViewModel(),
        ContainerHost<ChatState, ChatEffect> {
        override val container =
            container<ChatState, ChatEffect>(ChatState()) {
                observeMessages()
            }

        fun onIntent(intent: ChatIntent) {
            when (intent) {
                is ChatIntent.DraftChanged -> reduceDraft(intent.value)
                ChatIntent.SendClicked -> handleSend()
                ChatIntent.ErrorDismissed -> dismissError()
                ChatIntent.ClearHistoryClicked -> clearHistory()
            }
        }

        private fun observeMessages() {
            observeChatMessagesUseCase()
                .onEach { messages -> intent { reduce { state.copy(messages = messages) } } }
                .launchIn(viewModelScope)
        }

        private fun reduceDraft(value: String) =
            intent {
                reduce { state.copy(draft = value) }
            }

        private fun handleSend() =
            intent {
                val prompt = state.draft
                if (prompt.isBlank() || state.isSending) return@intent
                reduce { state.copy(draft = "", isSending = true, errorMessage = null) }
                val result = sendChatMessageUseCase(prompt)
                reduce { state.copy(isSending = false) }
                result.exceptionOrNull()?.let { error ->
                    val message = error.message.orEmpty().ifBlank { "Gagal mengirim pesan." }
                    reduce { state.copy(errorMessage = message) }
                    postSideEffect(ChatEffect.ShowError(message))
                }
            }

        private fun dismissError() =
            intent {
                reduce { state.copy(errorMessage = null) }
            }

        private fun clearHistory() =
            intent {
                clearChatHistoryUseCase()
            }
    }
