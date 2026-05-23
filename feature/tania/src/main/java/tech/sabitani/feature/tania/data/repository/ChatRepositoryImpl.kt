package tech.sabitani.feature.tania.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import tech.sabitani.core.network.gemini.GeminiConfig
import tech.sabitani.feature.tania.data.mapper.extractText
import tech.sabitani.feature.tania.data.mapper.toGeminiContent
import tech.sabitani.feature.tania.data.remote.api.GeminiApi
import tech.sabitani.feature.tania.data.remote.dto.GenerateContentRequestDto
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.model.ChatRole
import tech.sabitani.feature.tania.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatRepositoryImpl
    @Inject
    constructor(
        private val api: GeminiApi,
        private val config: GeminiConfig,
    ) : ChatRepository {
        private val state = MutableStateFlow<List<ChatMessage>>(emptyList())

        override val messages: Flow<List<ChatMessage>> = state.asStateFlow()

        override suspend fun send(prompt: String): Result<Unit> {
            if (!config.isConfigured) {
                return Result.failure(IllegalStateException("Gemini API key belum dikonfigurasi."))
            }
            appendUser(prompt)
            return runCatching { withContext(Dispatchers.IO) { callGemini() } }
                .onSuccess(::appendAssistant)
                .map { }
        }

        override suspend fun clear() {
            state.value = emptyList()
        }

        private fun appendUser(prompt: String) {
            state.update { current -> current + newMessage(role = ChatRole.USER, text = prompt) }
        }

        private fun appendAssistant(text: String) {
            state.update { current -> current + newMessage(role = ChatRole.ASSISTANT, text = text) }
        }

        private suspend fun callGemini(): String {
            val history = state.value.map { it.toGeminiContent() }
            val response =
                api.generateContent(
                    model = config.model,
                    request = GenerateContentRequestDto(contents = history),
                )
            return response.candidates
                .firstOrNull()
                ?.content
                ?.extractText()
                .orEmpty()
        }

        private fun newMessage(
            role: ChatRole,
            text: String,
        ): ChatMessage =
            ChatMessage(
                id = UUID.randomUUID().toString(),
                role = role,
                text = text,
                createdAt = Clock.System.now(),
            )
    }
