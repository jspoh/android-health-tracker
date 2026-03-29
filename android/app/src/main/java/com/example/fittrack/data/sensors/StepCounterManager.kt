package com.example.fittrack.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepCounterManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount.asStateFlow()

    private var baselineSteps: Int? = null

    fun isAvailable(): Boolean = stepSensor != null

    fun startCounting() {
        baselineSteps = null
        _stepCount.value = 0
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopCounting(): Int {
        sensorManager.unregisterListener(this)
        return _stepCount.value
    }

    override fun onSensorChanged(event: SensorEvent) {
        val totalSteps = event.values[0].toInt()
        if (baselineSteps == null) {
            baselineSteps = totalSteps
        }
        _stepCount.value = totalSteps - (baselineSteps ?: totalSteps)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { /* no-op */ }
}
