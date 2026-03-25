package com.example.fittrack.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: Int,
    val start: String,
    val end: String,
    @ColumnInfo(name = "activity_type") val activityType: String,
    @ColumnInfo(name = "steps_taken") val stepsTaken: Int,
    @ColumnInfo(name = "max_hr") val maxHr: Int,
    val notes: String
)
