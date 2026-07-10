package com.happylens.ai_bmi_calculator.presentation.profile

import androidx.lifecycle.ViewModel
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {
    val currentProfileName: StateFlow<String> = BmiRepository.currentProfileName

    fun selectProfile(name: String) {
        BmiRepository.updateProfileName(name)
    }
}
