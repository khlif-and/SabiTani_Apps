package tech.sabitani.feature.tania.domain.model

import kotlinx.datetime.Instant

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val text: String,
    val createdAt: Instant,
)
