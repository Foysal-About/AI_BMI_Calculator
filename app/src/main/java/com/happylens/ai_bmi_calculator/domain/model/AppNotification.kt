package com.happylens.ai_bmi_calculator.domain.model

import java.util.Date

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val date: Date,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.GENERAL
)

enum class NotificationType {
    REMINDER,
    ACHIEVEMENT,
    INSIGHT,
    GENERAL
}
