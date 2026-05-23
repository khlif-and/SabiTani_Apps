package tech.sabitani.core.network.gemini

data class GeminiConfig(
    val apiKey: String,
    val baseUrl: String,
    val model: String,
) {
    val isConfigured: Boolean get() = apiKey.isNotBlank()
}
