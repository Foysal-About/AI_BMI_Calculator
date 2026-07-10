package com.happylens.ai_bmi_calculator.data.repository

import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object BmiRepository {
    private val _records = MutableStateFlow<List<BmiRecord>>(emptyList())
    val records: StateFlow<List<BmiRecord>> = _records.asStateFlow()

    fun addRecord(record: BmiRecord) {
        _records.value = listOf(record) + _records.value
    }

    fun deleteRecord(recordId: String) {
        _records.value = _records.value.filter { it.id != recordId }
    }

    fun getLatestRecord(): BmiRecord? = _records.value.firstOrNull()

    private val _targetWeight = MutableStateFlow(65.0f)
    val targetWeight: StateFlow<Float> = _targetWeight.asStateFlow()

    fun updateTargetWeight(weight: Float) {
        _targetWeight.value = weight
    }

    private val _currentProfileName = MutableStateFlow("Foysal")
    val currentProfileName: StateFlow<String> = _currentProfileName.asStateFlow()

    fun updateProfileName(name: String) {
        _currentProfileName.value = name
    }
}
