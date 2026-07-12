package com.happylens.ai_bmi_calculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.domain.model.UserProfile

@Entity(tableName = "user_profiles")
data class ProfileEntity(
    @PrimaryKey
    val name: String,
    val age: Int,
    val gender: String,
    val startingWeight: Float,
    val targetWeight: Float,
    val isTracked: Boolean,
    val lastHeight: Float = 170f,
    val lastWeight: Float = 68f,
    val lastHeightUnit: String = "cm",
    val lastWeightUnit: String = "kg"
)

fun ProfileEntity.toDomain() = UserProfile(
    name = name,
    age = age,
    gender = Gender.valueOf(gender),
    startingWeight = startingWeight,
    targetWeight = targetWeight,
    isTracked = isTracked,
    lastHeight = lastHeight,
    lastWeight = lastWeight,
    lastHeightUnit = lastHeightUnit,
    lastWeightUnit = lastWeightUnit
)

fun UserProfile.toEntity() = ProfileEntity(
    name = name,
    age = age,
    gender = gender.name,
    startingWeight = startingWeight,
    targetWeight = targetWeight,
    isTracked = isTracked,
    lastHeight = lastHeight,
    lastWeight = lastWeight,
    lastHeightUnit = lastHeightUnit,
    lastWeightUnit = lastWeightUnit
)
