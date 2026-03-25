package com.example.fittrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fittrack.data.local.entity.ActivityEntity

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<ActivityEntity>)

    @Query("SELECT * FROM activities WHERE start LIKE :date || '%' ORDER BY start DESC")
    suspend fun getActivitiesForDate(date: String): List<ActivityEntity>

    @Query("SELECT * FROM activities WHERE start >= :start AND start <= :end ORDER BY start DESC")
    suspend fun getActivitiesInRange(start: String, end: String): List<ActivityEntity>

    @Query("DELETE FROM activities WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM activities ORDER BY start DESC LIMIT :limit")
    suspend fun getRecentActivities(limit: Int = 10): List<ActivityEntity>
}
