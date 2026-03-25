package com.example.fittrack.domain.usecase.steps

import com.example.fittrack.domain.model.Steps
import com.example.fittrack.domain.repository.StepsRepository
import javax.inject.Inject

class SyncStepsUseCase @Inject constructor(
    private val stepsRepository: StepsRepository
) {
    suspend operator fun invoke(date: String, steps: Int): Result<Steps> =
        runCatching { stepsRepository.syncSteps(date, steps) }
}
