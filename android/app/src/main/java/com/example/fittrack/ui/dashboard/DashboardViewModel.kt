package com.example.fittrack.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.core.utils.DateUtils
import com.example.fittrack.data.sensors.StepCounterManager
import com.example.fittrack.domain.model.Activity
import com.example.fittrack.domain.model.Steps
import com.example.fittrack.domain.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val todaySteps: Steps? = null,
    val recentActivities: List<Activity> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val stepCounterManager: StepCounterManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        stepCounterManager.startDailyTracking()
        viewModelScope.launch {
            stepCounterManager.dailySteps.collect { sensorSteps ->
                val current = _uiState.value.todaySteps?.steps ?: 0
                if (sensorSteps > current) {
                    _uiState.value = _uiState.value.copy(
                        todaySteps = Steps(DateUtils.today(), sensorSteps)
                    )
                }
            }
        }
        loadDashboard()
    }

    override fun onCleared() {
        super.onCleared()
        stepCounterManager.stopDailyTracking()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val today = DateUtils.today()
                val activities = activityRepository.getActivitiesForDate(today)
                val activitySteps = activities.sumOf { it.stepsTaken }
                val sensorSteps = stepCounterManager.dailySteps.value
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todaySteps = Steps(today, maxOf(sensorSteps, activitySteps)),
                    recentActivities = activities
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load dashboard"
                )
            }
        }
    }
}
