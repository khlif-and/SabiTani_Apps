package tech.sabitani.feature.tania.domain.model

data class KnowledgeEntry(
    val id: String,
    val category: KnowledgeCategory,
    val title: String,
    val summary: String,
    val keywords: List<String>,
    val body: String,
)
