package com.happylens.ai_bmi_calculator.domain.model

data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val targetWeight: Float = 65.0f,
    val isTracked: Boolean = true
)

enum class Gender {
    MALE, FEMALE
}
