package tech.sabitani.feature.auth.domain.usecase

import javax.inject.Inject
import tech.sabitani.feature.auth.domain.model.AuthenticatedUser
import tech.sabitani.feature.auth.domain.repository.AuthRepository

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthenticatedUser> =
        repository.login(email, password)
}
