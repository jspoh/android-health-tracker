package com.example.fittrack.data.remote.api

import com.example.fittrack.data.remote.dto.DailyStepsResponse
import com.example.fittrack.data.remote.dto.StepsSyncPayload
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface StepsApiService {
    @POST("steps/sync")
    suspend fun syncSteps(@Body payload: StepsSyncPayload): DailyStepsResponse

    @GET("steps/date/{day}")
    suspend fun getStepsForDate(@Path("day") day: String): DailyStepsResponse

    @GET("steps/range")
    suspend fun getStepsInRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): List<DailyStepsResponse>
}
