package tech.sabitani.feature.tania.domain.usecase

import tech.sabitani.feature.tania.domain.repository.ChatRepository
import javax.inject.Inject

class ClearChatHistoryUseCase
    @Inject
    constructor(
        private val repository: ChatRepository,
    ) {
        suspend operator fun invoke() = repository.clear()
    }
