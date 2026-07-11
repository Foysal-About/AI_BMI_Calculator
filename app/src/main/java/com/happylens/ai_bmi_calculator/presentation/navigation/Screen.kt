package com.happylens.ai_bmi_calculator.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Progress : Screen("progress")
    object AI : Screen("ai")
    object Calculator : Screen("calculator")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object History : Screen("history")
    object Notification : Screen("notification")
}
