package tech.sabitani.feature.tania.domain.usecase

import tech.sabitani.feature.tania.domain.model.KnowledgeEntry
import tech.sabitani.feature.tania.domain.repository.KnowledgeRepository
import javax.inject.Inject

class SearchKnowledgeUseCase
    @Inject
    constructor(
        private val repository: KnowledgeRepository,
    ) {
        suspend operator fun invoke(
            query: String,
            limit: Int = DEFAULT_LIMIT,
        ): List<KnowledgeEntry> {
            val tokens = query.tokenize()
            if (tokens.isEmpty()) return emptyList()
            return repository
                .loadAll()
                .asSequence()
                .map { entry -> entry to score(entry = entry, tokens = tokens) }
                .filter { (_, score) -> score > 0 }
                .sortedByDescending { (_, score) -> score }
                .take(limit)
                .map { (entry, _) -> entry }
                .toList()
        }

        private fun score(
            entry: KnowledgeEntry,
            tokens: List<String>,
        ): Int {
            val haystack = (entry.keywords + entry.title + entry.summary).joinToString(" ").lowercase()
            return tokens.sumOf { token ->
                when {
                    entry.keywords.any { it.equals(token, ignoreCase = true) } -> KEYWORD_HIT_SCORE
                    entry.title.contains(token, ignoreCase = true) -> TITLE_HIT_SCORE
                    haystack.contains(token) -> CONTENT_HIT_SCORE
                    else -> 0
                }
            }
        }

        private fun String.tokenize(): List<String> =
            lowercase()
                .split(Regex("[^a-z0-9]+"))
                .filter { it.length >= MIN_TOKEN_LENGTH }

        private companion object {
            const val DEFAULT_LIMIT = 5
            const val KEYWORD_HIT_SCORE = 5
            const val TITLE_HIT_SCORE = 3
            const val CONTENT_HIT_SCORE = 1
            const val MIN_TOKEN_LENGTH = 3
        }
    }
