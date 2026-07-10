package com.happylens.ai_bmi_calculator.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.happylens.ai_bmi_calculator.presentation.onboarding.OnboardingScreen
import com.happylens.ai_bmi_calculator.presentation.home.HomeScreen
import com.happylens.ai_bmi_calculator.presentation.progress.ProgressScreen
import com.happylens.ai_bmi_calculator.presentation.progress.HistoryScreen
import com.happylens.ai_bmi_calculator.presentation.ai.AIScreen
import com.happylens.ai_bmi_calculator.presentation.calculator.CalculatorScreen
import com.happylens.ai_bmi_calculator.presentation.profile.ProfileScreen
import com.happylens.ai_bmi_calculator.presentation.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Home.route) {
            HomeScreen(
                onCalculateClick = {
                    navController.navigate(Screen.Calculator.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.Progress.route) {
            ProgressScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.AI.route) {
            AIScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.Calculator.route) {
            CalculatorScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.History.route) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(Screen.Home.route) { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
    }
}
