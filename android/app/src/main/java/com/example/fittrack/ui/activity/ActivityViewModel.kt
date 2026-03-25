package com.example.fittrack.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.core.utils.DateUtils
import com.example.fittrack.data.sensors.ActivityRecognitionManager
import com.example.fittrack.domain.usecase.activity.LogActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class ActivityUiState(
    val isTracking: Boolean = false,
    val currentActivityType: String = "UNKNOWN",
    val isSaving: Boolean = false,
    val savedSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityRecognitionManager: ActivityRecognitionManager,
    private val logActivityUseCase: LogActivityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private var trackingStartTime: LocalDateTime? = null

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
    }

    fun startTracking() {
        trackingStartTime = LocalDateTime.now()
        activityRecognitionManager.startTracking()
    }

    fun stopAndSave(stepsTaken: Int, maxHr: Int, notes: String) {
        val start = trackingStartTime ?: return
        val end = LocalDateTime.now()
        activityRecognitionManager.stopTracking()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            logActivityUseCase(
                start = DateUtils.formatDateTime(start),
                end = DateUtils.formatDateTime(end),
                activityType = _uiState.value.currentActivityType,
                stepsTaken = stepsTaken,
                maxHr = maxHr,
                notes = notes
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSaving = false, savedSuccess = true)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isSaving = false, error = it.message)
            }
        }
    }

    fun hasPermission() = activityRecognitionManager.hasPermission()
}
