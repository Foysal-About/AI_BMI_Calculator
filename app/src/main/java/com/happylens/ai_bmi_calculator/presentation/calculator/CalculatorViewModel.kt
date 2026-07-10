package com.happylens.ai_bmi_calculator.presentation.calculator

import androidx.lifecycle.ViewModel
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import java.util.Date
import java.util.UUID

class CalculatorViewModel : ViewModel() {
    fun saveBmiRecord(
        weight: Float,
        height: Float,
        bmi: Float,
        category: String,
        weightUnit: String,
        heightUnit: String
    ) {
        val record = BmiRecord(
            id = UUID.randomUUID().toString(),
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
