package tech.sabitani.feature.tania.domain.repository

import tech.sabitani.feature.tania.domain.model.KnowledgeEntry

interface KnowledgeRepository {
    suspend fun loadAll(): List<KnowledgeEntry>
}
