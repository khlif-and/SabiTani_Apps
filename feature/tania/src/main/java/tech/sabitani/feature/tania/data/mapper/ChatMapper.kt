package tech.sabitani.feature.tania.data.mapper

import tech.sabitani.feature.tania.data.remote.dto.GeminiContentDto
import tech.sabitani.feature.tania.data.remote.dto.GeminiPartDto
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.model.ChatRole

private const val GEMINI_ROLE_USER = "user"
private const val GEMINI_ROLE_MODEL = "model"

internal fun ChatMessage.toGeminiContent(): GeminiContentDto =
    GeminiContentDto(
        role = role.toGeminiRole(),
        parts = listOf(GeminiPartDto(text = text)),
    )

internal fun ChatRole.toGeminiRole(): String =
    when (this) {
        ChatRole.USER -> GEMINI_ROLE_USER
        ChatRole.ASSISTANT -> GEMINI_ROLE_MODEL
    }

internal fun GeminiContentDto.extractText(): String = parts.mapNotNull { it.text }.joinToString(separator = "\n").trim()
