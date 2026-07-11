package com.happylens.ai_bmi_calculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.happylens.ai_bmi_calculator.presentation.navigation.NavGraph
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.theme.AI_BMI_CalculatorTheme

@Composable
fun App() {
    val navController = rememberNavController()
    AI_BMI_CalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                startDestination = Screen.Onboarding.route
            )
        }
    }
}
