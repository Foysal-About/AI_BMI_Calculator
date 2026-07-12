package com.happylens.ai_bmi_calculator.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onAgeChanged(age: Int) {
        _uiState.update { it.copy(age = age) }
    }

    fun onGenderChanged(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val nameToSave = _uiState.value.name.ifBlank { "Guest User" }
            val profile = UserProfile(
                name = nameToSave,
                age = _uiState.value.age,
                gender = _uiState.value.gender,
                isTracked = true
            )
            BmiRepository.updateUserProfile(profile)
            BmiRepository.updateProfileName(profile.name)
            BmiRepository.setOnboardingCompleted(true)
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            BmiRepository.updateProfileName("Guest User")
            BmiRepository.setOnboardingCompleted(true)
        }
    }
}

data class OnboardingUiState(
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.MALE
)
