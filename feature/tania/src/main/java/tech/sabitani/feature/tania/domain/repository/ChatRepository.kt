package tech.sabitani.feature.tania.domain.repository

import kotlinx.coroutines.flow.Flow
import tech.sabitani.feature.tania.domain.model.ChatMessage

interface ChatRepository {
    val messages: Flow<List<ChatMessage>>

    suspend fun send(prompt: String): Result<Unit>

    suspend fun clear()
}
