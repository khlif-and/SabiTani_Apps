package tech.sabitani.feature.tania.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentResponseDto(
    val candidates: List<GeminiCandidateDto> = emptyList(),
    val promptFeedback: GeminiPromptFeedbackDto? = null,
    val usageMetadata: GeminiUsageMetadataDto? = null,
)

@Serializable
data class GeminiCandidateDto(
    val content: GeminiContentDto? = null,
    val finishReason: String? = null,
    val index: Int? = null,
)

@Serializable
data class GeminiPromptFeedbackDto(
    val blockReason: String? = null,
)

@Serializable
data class GeminiUsageMetadataDto(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null,
)
