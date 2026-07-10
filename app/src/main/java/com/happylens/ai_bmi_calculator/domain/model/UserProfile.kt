package com.happylens.ai_bmi_calculator.domain.model

data class UserProfile(
    val name: String = "",
    val age: Int = 25,
    val gender: Gender = Gender.MALE
)

enum class Gender {
    MALE, FEMALE
}
