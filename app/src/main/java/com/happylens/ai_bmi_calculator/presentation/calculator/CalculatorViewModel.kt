package com.happylens.ai_bmi_calculator.presentation.calculator

import androidx.lifecycle.ViewModel
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import java.util.Date
import java.util.UUID

import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.domain.model.Gender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CalculatorViewModel : ViewModel() {
    private val _gender = MutableStateFlow(Gender.MALE)
    val gender = _gender.asStateFlow()

    private val _age = MutableStateFlow(25)
    val age = _age.asStateFlow()

    private val _height = MutableStateFlow(170f)
    val height = _height.asStateFlow()

    private val _weight = MutableStateFlow(68f)
    val weight = _weight.asStateFlow()

    private val _heightUnit = MutableStateFlow("cm")
    val heightUnit = _heightUnit.asStateFlow()

    private val _weightUnit = MutableStateFlow("kg")
    val weightUnit = _weightUnit.asStateFlow()

    init {
        loadLastValues()
    }

    private fun loadLastValues() {
        viewModelScope.launch {
            val profile = BmiRepository.userProfile.first()
            _gender.value = profile.gender
            _age.value = profile.age
            _height.value = profile.lastHeight
            _weight.value = profile.lastWeight
            _heightUnit.value = profile.lastHeightUnit
            _weightUnit.value = profile.lastWeightUnit
        }
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
    }

    fun updateAge(age: Int) {
        _age.value = age
    }

    fun updateHeight(height: Float) {
        _height.value = height
    }

    fun updateWeight(weight: Float) {
        _weight.value = weight
    }

    fun updateHeightUnit(unit: String) {
        _heightUnit.value = unit
    }

    fun updateWeightUnit(unit: String) {
        _weightUnit.value = unit
    }

    fun saveBmiRecord(
        weight: Float,
        height: Float,
        bmi: Float,
        category: String,
        weightUnit: String,
        heightUnit: String,
        goalWeight: Float? = null
    ) {
        viewModelScope.launch {
            val userProfile = BmiRepository.userProfile.first()
            
            // Update profile with latest values (Always do this, even for Guest)
            val updatedProfile = userProfile.copy(
                gender = _gender.value,
                age = _age.value,
                lastHeight = _height.value,
                lastWeight = _weight.value,
                lastHeightUnit = _heightUnit.value,
                lastWeightUnit = _weightUnit.value
            )
            BmiRepository.updateUserProfile(updatedProfile)
            
            // Auto-update target weight if it's not set yet
            if (userProfile.isTracked && goalWeight != null && userProfile.targetWeight == 0f) {
                val currentWeightInKg = if (weightUnit == "kg") weight else weight / 2.20462f
                val targetWeightInKg = if (weightUnit == "kg") goalWeight else goalWeight / 2.20462f
                BmiRepository.updateWeightGoal(targetWeightInKg, currentWeightInKg)
            }

            if (!userProfile.isTracked) return@launch // Don't save record history for guest profiles

            val record = BmiRecord(
                id = UUID.randomUUID().toString(),
                profileName = userProfile.name,
                weight = weight,
                height = height,
                bmi = bmi,
                date = Date(),
                category = category,
                weightUnit = weightUnit,
                heightUnit = heightUnit
            )
            BmiRepository.addRecord(record)
        }
    }
}
