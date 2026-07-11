package com.happylens.ai_bmi_calculator.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {
    val bmiRecords: StateFlow<List<BmiRecord>> = BmiRepository.records
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val profileName: StateFlow<String> = BmiRepository.currentProfileName
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Foysal"
        )

    val targetWeight: StateFlow<Float> = BmiRepository.targetWeight

    val startingWeight: StateFlow<Float?> = bmiRecords.map { records ->
        records.lastOrNull()?.weight
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectProfile(name: String) {
        BmiRepository.updateProfileName(name)
    }
}
