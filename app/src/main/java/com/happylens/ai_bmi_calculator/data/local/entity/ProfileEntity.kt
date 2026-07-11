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
    val targetWeight: Float,
    val isTracked: Boolean
)

fun ProfileEntity.toDomain() = UserProfile(
    name = name,
    age = age,
    gender = Gender.valueOf(gender),
    targetWeight = targetWeight,
    isTracked = isTracked
)

fun UserProfile.toEntity() = ProfileEntity(
    name = name,
    age = age,
    gender = gender.name,
    targetWeight = targetWeight,
    isTracked = isTracked
)
