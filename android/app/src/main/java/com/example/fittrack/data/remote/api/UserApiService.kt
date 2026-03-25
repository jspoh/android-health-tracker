package com.example.fittrack.data.remote.api

import com.example.fittrack.data.remote.dto.CreateUserPayload
import com.example.fittrack.data.remote.dto.GetUserResponse
import com.example.fittrack.data.remote.dto.LoginPayload
import com.example.fittrack.data.remote.dto.LoginResponse
import com.example.fittrack.data.remote.dto.UpdateUserPayload
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApiService {
    @POST("user/")
    suspend fun createUser(@Body payload: CreateUserPayload): GetUserResponse

    @POST("user/login")
    suspend fun login(@Body payload: LoginPayload): LoginResponse

    @GET("user/me")
    suspend fun getMe(): GetUserResponse

    @PATCH("user/")
    suspend fun updateUser(@Body payload: UpdateUserPayload): GetUserResponse
}
