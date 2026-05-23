package tech.sabitani.feature.tania.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import tech.sabitani.feature.tania.data.local.dto.KnowledgeEntryDto
import tech.sabitani.feature.tania.data.mapper.toDomain
import tech.sabitani.feature.tania.domain.model.KnowledgeCategory
import tech.sabitani.feature.tania.domain.model.KnowledgeEntry
import javax.inject.Inject

internal class KnowledgeAssetLoader
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val json: Json,
    ) {
        fun loadAll(): List<KnowledgeEntry> =
            KnowledgeCategory.entries.flatMap { category ->
                readCategory(category).map { it.toDomain(category) }
            }

        private fun readCategory(category: KnowledgeCategory): List<KnowledgeEntryDto> {
            val fileName = category.assetFileName()
            val raw =
                context.assets
                    .open(fileName)
                    .bufferedReader()
                    .use { it.readText() }
            return json.decodeFromString(raw)
        }

        private fun KnowledgeCategory.assetFileName(): String =
            when (this) {
                KnowledgeCategory.DISEASE -> "knowledge/diseases.json"
                KnowledgeCategory.FERTILIZER -> "knowledge/fertilizers.json"
                KnowledgeCategory.PESTICIDE -> "knowledge/pesticides.json"
                KnowledgeCategory.VARIETY -> "knowledge/varieties.json"
            }
    }
