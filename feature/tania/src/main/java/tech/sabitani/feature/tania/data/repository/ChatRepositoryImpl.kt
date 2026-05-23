package tech.sabitani.feature.tania.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import tech.sabitani.core.database.dao.ChatMessageDao
import tech.sabitani.core.network.gemini.GeminiConfig
import tech.sabitani.feature.tania.data.mapper.extractText
import tech.sabitani.feature.tania.data.mapper.toDomain
import tech.sabitani.feature.tania.data.mapper.toEntity
import tech.sabitani.feature.tania.data.remote.ChatPromptBuilder
import tech.sabitani.feature.tania.data.remote.api.GeminiApi
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
        private val dao: ChatMessageDao,
        private val promptBuilder: ChatPromptBuilder,
        private val clock: Clock,
    ) : ChatRepository {
        override val messages: Flow<List<ChatMessage>> =
            dao.observeAll().map { entities -> entities.map { it.toDomain() } }

        override suspend fun send(prompt: String): Result<Unit> {
            if (!config.isConfigured) {
                return Result.failure(IllegalStateException("Gemini API key belum dikonfigurasi."))
            }
            val historySnapshot = dao.getAll().map { it.toDomain() }
            val userMessage = newMessage(role = ChatRole.USER, text = prompt)
            dao.insert(userMessage.toEntity())

            return runCatching { withContext(Dispatchers.IO) { generate(prompt, historySnapshot) } }
                .onSuccess { reply ->
                    dao.insert(newMessage(role = ChatRole.ASSISTANT, text = reply).toEntity())
                }.map { }
        }

        override suspend fun clear() {
            dao.deleteAll()
        }

        private suspend fun generate(
            prompt: String,
            history: List<ChatMessage>,
        ): String {
            val request = promptBuilder.build(userPrompt = prompt, history = history)
            val response = api.generateContent(model = config.model, request = request)
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
                createdAt = clock.now(),
            )
    }
