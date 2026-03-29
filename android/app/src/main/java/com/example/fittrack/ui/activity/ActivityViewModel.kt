package com.example.fittrack.ui.activity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.core.content.ContextCompat
import com.example.fittrack.core.utils.DateUtils
import com.example.fittrack.data.sensors.ActivityRecognitionManager
import com.example.fittrack.data.sensors.StepCounterManager
import com.example.fittrack.domain.repository.StepsRepository
import com.example.fittrack.domain.usecase.activity.LogActivityUseCase
import com.example.fittrack.domain.usecase.steps.SyncStepsUseCase
import com.example.fittrack.service.ActivityTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class ActivityUiState(
    val isTracking: Boolean = false,
    val currentActivityType: String = "UNKNOWN",
    val stepCount: Int = 0,
    val elapsedSeconds: Long = 0,
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val activityRecognitionManager: ActivityRecognitionManager,
    private val stepCounterManager: StepCounterManager,
    private val logActivityUseCase: LogActivityUseCase,
    private val syncStepsUseCase: SyncStepsUseCase,
    private val stepsRepository: StepsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private var trackingStartTime: LocalDateTime? = null
    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            activityRecognitionManager.currentActivity.collect { type ->
                _uiState.value = _uiState.value.copy(currentActivityType = type)
            }
        }
        viewModelScope.launch {
            activityRecognitionManager.isTracking.collect { tracking ->
                _uiState.value = _uiState.value.copy(isTracking = tracking)
            }
        }
        viewModelScope.launch {
            stepCounterManager.stepCount.collect { steps ->
                _uiState.value = _uiState.value.copy(stepCount = steps)
            }
        }
    }

    fun startTracking() {
        trackingStartTime = LocalDateTime.now()
        ContextCompat.startForegroundService(context, ActivityTrackingService.startIntent(context))
        timerJob = viewModelScope.launch {
            val startMillis = System.currentTimeMillis()
            while (isActive) {
                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = (System.currentTimeMillis() - startMillis) / 1000
                )
                delay(1000)
            }
        }
    }

    fun stopAndSave() {
        val start = trackingStartTime ?: return
        val end = LocalDateTime.now()
        val finalSteps = stepCounterManager.stopCounting()
        context.startService(ActivityTrackingService.stopIntent(context))
        timerJob?.cancel()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            logActivityUseCase(
                start = DateUtils.formatDateTime(start),
                end = DateUtils.formatDateTime(end),
                activityType = _uiState.value.currentActivityType,
                stepsTaken = finalSteps,
                maxHr = 0,
                notes = ""
            ).onSuccess {
                syncTodaySteps(finalSteps)
                _uiState.value = _uiState.value.copy(isSaving = false, savedSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isSaving = false, error = it.message)
            }
        }
    }

    fun hasPermission() = activityRecognitionManager.hasPermission()

    private suspend fun syncTodaySteps(sessionStepsFallback: Int) {
        val today = DateUtils.today()
        val syncedSteps = stepsRepository.getStepsForDate(today)?.steps ?: 0
        val sensorSteps = stepCounterManager.dailySteps.value
        val stepsToSync = maxOf(sensorSteps, syncedSteps + sessionStepsFallback)
        syncStepsUseCase(today, stepsToSync)
    }
}
