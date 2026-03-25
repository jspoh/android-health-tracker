package com.example.fittrack.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ActivityLogPayload(
    val start: String,
    val end: String,
    @SerializedName("activity_type") val activityType: String,
    @SerializedName("steps_taken") val stepsTaken: Int,
    @SerializedName("max_hr") val maxHr: Int,
    val notes: String
)

data class ActivityResponse(
    val id: Int,
    val start: String,
    val end: String,
    @SerializedName("activity_type") val activityType: String,
    @SerializedName("steps_taken") val stepsTaken: Int,
    @SerializedName("max_hr") val maxHr: Int,
    val notes: String
)
