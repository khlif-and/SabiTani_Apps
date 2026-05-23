package tech.sabitani.feature.tania.data.mapper

import tech.sabitani.feature.tania.data.local.dto.KnowledgeEntryDto
import tech.sabitani.feature.tania.domain.model.KnowledgeCategory
import tech.sabitani.feature.tania.domain.model.KnowledgeEntry

internal fun KnowledgeEntryDto.toDomain(category: KnowledgeCategory): KnowledgeEntry =
    KnowledgeEntry(
        id = id,
        category = category,
        title = title,
        summary = summary,
        keywords = keywords,
        body = body,
    )
