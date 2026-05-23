package tech.sabitani.feature.tania.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class KnowledgeEntryDto(
    val id: String,
    val title: String,
    val summary: String,
    val keywords: List<String>,
    val body: String,
)
