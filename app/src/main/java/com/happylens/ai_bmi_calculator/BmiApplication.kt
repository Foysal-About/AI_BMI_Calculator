package com.happylens.ai_bmi_calculator

import android.app.Application
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository

class BmiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BmiRepository.initialize(this)
    }
}
