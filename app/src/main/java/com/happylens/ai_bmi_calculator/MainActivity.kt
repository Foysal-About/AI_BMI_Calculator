package com.happylens.ai_bmi_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.happylens.ai_bmi_calculator.presentation.navigation.NavGraph
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.theme.AI_BMI_CalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AI_BMI_CalculatorTheme {
                val viewModel: MainViewModel = viewModel()
                val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isOnboardingCompleted != null) {
                        val navController = rememberNavController()
                        NavGraph(
                            navController = navController,
                            startDestination = if (isOnboardingCompleted == true) Screen.Home.route else Screen.Onboarding.route
                        )
                    }
                }
            }
        }
    }
}
