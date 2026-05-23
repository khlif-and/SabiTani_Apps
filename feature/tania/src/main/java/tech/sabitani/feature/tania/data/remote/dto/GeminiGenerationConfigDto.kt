package tech.sabitani.feature.tania.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeminiGenerationConfigDto(
    val temperature: Float? = null,
    val topK: Int? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null,
    val candidateCount: Int? = null,
)

@Serializable
data class GeminiSafetySettingDto(
    val category: String,
    val threshold: String,
)
