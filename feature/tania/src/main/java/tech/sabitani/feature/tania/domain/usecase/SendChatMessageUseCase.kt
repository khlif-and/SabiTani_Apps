package tech.sabitani.feature.tania.domain.usecase

import tech.sabitani.feature.tania.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatMessageUseCase
    @Inject
    constructor(
        private val repository: ChatRepository,
    ) {
        suspend operator fun invoke(prompt: String): Result<Unit> {
            val trimmed = prompt.trim()
            require(trimmed.isNotEmpty()) { "Pesan tidak boleh kosong." }
            return repository.send(trimmed)
        }
    }
