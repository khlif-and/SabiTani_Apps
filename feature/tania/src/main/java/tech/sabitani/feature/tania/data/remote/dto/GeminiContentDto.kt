package tech.sabitani.feature.tania.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiContentDto(
    val role: String,
    val parts: List<GeminiPartDto>,
)

@Serializable
data class GeminiPartDto(
    val text: String? = null,
    val inlineData: GeminiInlineDataDto? = null,
)

@Serializable
data class GeminiInlineDataDto(
    val mimeType: String,
    val data: String,
)
