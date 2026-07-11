package com.happylens.ai_bmi_calculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import java.util.Date

@Entity(tableName = "bmi_records")
data class BmiEntity(
    @PrimaryKey
    val id: String,
    val profileName: String, // Added to differentiate profiles
    val weight: Float,
    val height: Float,
    val bmi: Float,
    val date: Long, // Store date as Long for Room
    val category: String,
    val weightUnit: String,
    val heightUnit: String
)

fun BmiEntity.toDomain() = BmiRecord(
    id = id,
    profileName = profileName,
    weight = weight,
    height = height,
    bmi = bmi,
    date = Date(date),
    category = category,
    weightUnit = weightUnit,
    heightUnit = heightUnit
)

fun BmiRecord.toEntity() = BmiEntity(
    id = id,
    profileName = profileName,
    weight = weight,
    height = height,
    bmi = bmi,
    date = date.time,
    category = category,
    weightUnit = weightUnit,
    heightUnit = heightUnit
)
