package tech.sabitani.feature.tania.domain.usecase

import kotlinx.coroutines.flow.Flow
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.repository.ChatRepository
import javax.inject.Inject

class ObserveChatMessagesUseCase
    @Inject
    constructor(
        private val repository: ChatRepository,
    ) {
        operator fun invoke(): Flow<List<ChatMessage>> = repository.messages
    }
