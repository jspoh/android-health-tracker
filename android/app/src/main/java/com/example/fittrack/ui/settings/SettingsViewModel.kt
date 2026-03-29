package com.example.fittrack.ui.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrack.data.preferences.SettingsRepository
import com.example.fittrack.data.sensors.ActivityRecognitionManager
import com.example.fittrack.service.ActivityTrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val autoTrackingEnabled: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val activityRecognitionManager: ActivityRecognitionManager
) : ViewModel() {

    companion object {
        private const val TAG = "ActivityAutoStart"
    }

    val uiState: StateFlow<SettingsUiState> =
        settingsRepository.autoTrackingEnabled
            .map { SettingsUiState(autoTrackingEnabled = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState()
            )

    fun setAutoTracking(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled && !activityRecognitionManager.hasPermission()) {
                Log.w(TAG, "Ignoring auto-tracking enable because permission is missing")
                return@launch
            }

            settingsRepository.setAutoTracking(enabled)
            activityRecognitionManager.setAutoTrackingEnabled(enabled)

            if (enabled) {
                Log.d(TAG, "Enabling auto tracking and registering transitions")
                activityRecognitionManager.registerAutoTransitions()
            } else {
                Log.d(TAG, "Disabling auto tracking and unregistering transitions")
                activityRecognitionManager.unregisterAutoTransitions()
                context.startService(ActivityTrackingService.stopIntent(context))
            }
        }
    }

    fun hasPermission() = activityRecognitionManager.hasPermission()

    fun hasNotificationPermission() = activityRecognitionManager.hasNotificationPermission()
}
