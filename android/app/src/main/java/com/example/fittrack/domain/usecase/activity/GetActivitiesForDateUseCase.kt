package com.example.fittrack.domain.usecase.activity

import com.example.fittrack.domain.model.Activity
import com.example.fittrack.domain.repository.ActivityRepository
import javax.inject.Inject

class GetActivitiesForDateUseCase @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    suspend operator fun invoke(date: String): Result<List<Activity>> =
        runCatching { activityRepository.getActivitiesForDate(date) }
}
