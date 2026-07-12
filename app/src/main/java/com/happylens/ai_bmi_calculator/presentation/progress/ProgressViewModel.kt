package com.happylens.ai_bmi_calculator.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import kotlinx.coroutines.flow.*

class ProgressViewModel : ViewModel() {
    val bmiRecords: StateFlow<List<BmiRecord>> = BmiRepository.records
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteRecord(recordId: String) {
        BmiRepository.deleteRecord(recordId)
    }

    val userProfile: StateFlow<UserProfile?> = BmiRepository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val currentBmi: StateFlow<Float?> = bmiRecords.map { records ->
        records.firstOrNull()?.bmi
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
