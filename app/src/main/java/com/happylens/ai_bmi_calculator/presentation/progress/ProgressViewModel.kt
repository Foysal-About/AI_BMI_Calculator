package com.happylens.ai_bmi_calculator.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import kotlinx.coroutines.flow.*

class ProgressViewModel : ViewModel() {
    val bmiRecords: StateFlow<List<BmiRecord>> = BmiRepository.records
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val targetWeight: StateFlow<Float> = BmiRepository.targetWeight
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 65.0f
        )

    fun updateTargetWeight(weight: Float) {
        BmiRepository.updateTargetWeight(weight)
    }

    fun deleteRecord(recordId: String) {
        BmiRepository.deleteRecord(recordId)
    }

    val currentWeight: StateFlow<Float?> = bmiRecords.map { records ->
        records.firstOrNull()?.weight
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val startingWeight: StateFlow<Float?> = bmiRecords.map { records ->
        records.lastOrNull()?.weight
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
