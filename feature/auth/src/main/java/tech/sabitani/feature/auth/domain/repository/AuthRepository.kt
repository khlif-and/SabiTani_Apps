package tech.sabitani.feature.auth.domain.repository

import tech.sabitani.feature.auth.domain.model.AuthenticatedUser

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<AuthenticatedUser>
}
