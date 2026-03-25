package com.example.fittrack.data.repository

import com.example.fittrack.data.local.dao.StepsDao
import com.example.fittrack.data.local.entity.StepsEntity
import com.example.fittrack.data.remote.api.StepsApiService
import com.example.fittrack.data.remote.dto.StepsSyncPayload
import com.example.fittrack.domain.model.Steps
import com.example.fittrack.domain.repository.StepsRepository
import javax.inject.Inject

class StepsRepositoryImpl @Inject constructor(
    private val stepsApiService: StepsApiService,
    private val stepsDao: StepsDao
) : StepsRepository {

    override suspend fun syncSteps(date: String, steps: Int): Steps {
        val response = stepsApiService.syncSteps(StepsSyncPayload(date, steps))
        stepsDao.insert(StepsEntity(response.date, response.steps))
        return Steps(response.date, response.steps)
    }

    override suspend fun getStepsForDate(date: String): Steps? {
        return try {
            val response = stepsApiService.getStepsForDate(date)
            stepsDao.insert(StepsEntity(response.date, response.steps))
            Steps(response.date, response.steps)
        } catch (e: Exception) {
            stepsDao.getStepsForDate(date)?.let { Steps(it.date, it.steps) }
        }
    }

    override suspend fun getStepsInRange(start: String, end: String): List<Steps> {
        return try {
            val response = stepsApiService.getStepsInRange(start, end)
            stepsDao.let { dao -> response.forEach { dao.insert(StepsEntity(it.date, it.steps)) } }
            response.map { Steps(it.date, it.steps) }
        } catch (e: Exception) {
            stepsDao.getStepsInRange(start, end).map { Steps(it.date, it.steps) }
        }
    }
}
