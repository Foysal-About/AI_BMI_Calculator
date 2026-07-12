package com.happylens.ai_bmi_calculator.domain.model

data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val startingWeight: Float = 0f,
    val targetWeight: Float = 0f,
    val isTracked: Boolean = true,
    val lastHeight: Float = 170f,
    val lastWeight: Float = 68f,
    val lastHeightUnit: String = "cm",
    val lastWeightUnit: String = "kg"
)

enum class Gender {
    MALE, FEMALE
}
