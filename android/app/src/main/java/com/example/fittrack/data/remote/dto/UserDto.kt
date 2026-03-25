package com.example.fittrack.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateUserPayload(
    val username: String,
    val email: String,
    val password: String
)

data class LoginPayload(
    val username: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer",
    val user: GetUserResponse
)

data class GetUserResponse(
    val id: Int,
    val username: String,
    val email: String
)

data class UpdateUserPayload(
    val username: String? = null,
    val email: String? = null,
    val password: String? = null,
    @SerializedName("step_target") val stepTarget: Int? = null
)
