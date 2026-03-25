package com.example.fittrack.data.remote.dto

data class StepsSyncPayload(
    val date: String,
    val steps: Int
)

data class DailyStepsResponse(
    val date: String,
    val steps: Int
)
