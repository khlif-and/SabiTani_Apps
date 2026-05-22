package tech.sabitani.core.analytics

data class AnalyticsEvent(
    val type: String,
    val params: Map<String, String> = emptyMap(),
)
