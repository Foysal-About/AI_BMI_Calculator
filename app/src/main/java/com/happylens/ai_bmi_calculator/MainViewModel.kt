package com.happylens.ai_bmi_calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {
    val isOnboardingCompleted: StateFlow<Boolean?> = BmiRepository.isOnboardingCompleted
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
}
