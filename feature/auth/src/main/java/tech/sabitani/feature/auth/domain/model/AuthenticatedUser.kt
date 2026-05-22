package tech.sabitani.feature.auth.domain.model

data class AuthenticatedUser(
    val id: String,
    val email: String,
    val displayName: String,
)
