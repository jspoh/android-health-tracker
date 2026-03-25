package com.example.fittrack.domain.usecase.auth

import com.example.fittrack.domain.model.User
import com.example.fittrack.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> =
        runCatching { userRepository.register(username, email, password) }
}
