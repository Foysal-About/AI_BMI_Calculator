package com.happylens.ai_bmi_calculator.domain.model

import java.util.Date

data class BmiRecord(
    val id: String,
    val profileName: String,
    val weight: Float,
    val height: Float,
    val bmi: Float,
    val date: Date,
    val category: String,
    val weightUnit: String,
    val heightUnit: String
)
