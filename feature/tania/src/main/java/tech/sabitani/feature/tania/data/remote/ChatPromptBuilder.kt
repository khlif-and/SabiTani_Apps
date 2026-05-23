package tech.sabitani.feature.tania.data.remote

import tech.sabitani.feature.tania.data.mapper.toGeminiContent
import tech.sabitani.feature.tania.data.remote.dto.GeminiContentDto
import tech.sabitani.feature.tania.data.remote.dto.GeminiGenerationConfigDto
import tech.sabitani.feature.tania.data.remote.dto.GeminiPartDto
import tech.sabitani.feature.tania.data.remote.dto.GenerateContentRequestDto
import tech.sabitani.feature.tania.domain.model.ChatMessage
import tech.sabitani.feature.tania.domain.model.KnowledgeCategory
import tech.sabitani.feature.tania.domain.model.KnowledgeEntry
import tech.sabitani.feature.tania.domain.prompt.TaniaSystemPrompt
import tech.sabitani.feature.tania.domain.usecase.SearchKnowledgeUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatPromptBuilder
    @Inject
    constructor(
        private val systemPrompt: TaniaSystemPrompt,
        private val searchKnowledge: SearchKnowledgeUseCase,
    ) {
        suspend fun build(
            userPrompt: String,
            history: List<ChatMessage>,
        ): GenerateContentRequestDto {
            val knowledge = searchKnowledge(userPrompt)
            val augmented = augmentPrompt(userPrompt = userPrompt, knowledge = knowledge)
            val contents =
                history.map { it.toGeminiContent() } +
                    GeminiContentDto(role = ROLE_USER, parts = listOf(GeminiPartDto(text = augmented)))
            return GenerateContentRequestDto(
                contents = contents,
                systemInstruction =
                    GeminiContentDto(
                        role = ROLE_USER,
                        parts = listOf(GeminiPartDto(text = systemPrompt.text)),
                    ),
                generationConfig =
                    GeminiGenerationConfigDto(
                        temperature = TEMPERATURE,
                        maxOutputTokens = MAX_OUTPUT_TOKENS,
                    ),
            )
        }

        private fun augmentPrompt(
            userPrompt: String,
            knowledge: List<KnowledgeEntry>,
        ): String {
            if (knowledge.isEmpty()) return userPrompt
            val references =
                knowledge.joinToString(separator = "\n\n") { entry ->
                    "- ${entry.title} (${entry.category.label()})\n  ${entry.body}"
                }
            return buildString {
                append(userPrompt)
                append("\n\n<referensi>\n")
                append(references)
                append("\n</referensi>")
            }
        }

        private fun KnowledgeCategory.label(): String =
            when (this) {
                KnowledgeCategory.DISEASE -> "penyakit/hama"
                KnowledgeCategory.FERTILIZER -> "pupuk"
                KnowledgeCategory.PESTICIDE -> "pestisida"
                KnowledgeCategory.VARIETY -> "varietas"
            }

        private companion object {
            const val ROLE_USER = "user"
            const val TEMPERATURE = 0.4f
            const val MAX_OUTPUT_TOKENS = 1024
        }
    }
