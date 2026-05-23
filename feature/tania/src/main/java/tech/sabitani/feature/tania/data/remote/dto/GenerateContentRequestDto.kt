package tech.sabitani.feature.tania.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentRequestDto(
    val contents: List<GeminiContentDto>,
    val systemInstruction: GeminiContentDto? = null,
    val generationConfig: GeminiGenerationConfigDto? = null,
    val safetySettings: List<GeminiSafetySettingDto>? = null,
)
