package tech.sabitani.feature.auth.data.repository

import kotlinx.coroutines.delay
import tech.sabitani.feature.auth.domain.model.AuthenticatedUser
import tech.sabitani.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

private const val FAKE_NETWORK_DELAY_MS = 800L

internal class FakeAuthRepository
    @Inject
    constructor() : AuthRepository {
        override suspend fun login(
            email: String,
            password: String,
        ): Result<AuthenticatedUser> {
            delay(FAKE_NETWORK_DELAY_MS)
            return if (password.length >= 6) {
                Result.success(
                    AuthenticatedUser(
                        id = "user-1",
                        email = email,
                        displayName = email.substringBefore('@'),
                    ),
                )
            } else {
                Result.failure(IllegalArgumentException("Password minimal 6 karakter"))
            }
        }
    }
