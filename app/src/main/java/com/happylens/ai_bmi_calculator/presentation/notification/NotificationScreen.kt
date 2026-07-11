package com.happylens.ai_bmi_calculator.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.happylens.ai_bmi_calculator.domain.model.AppNotification
import com.happylens.ai_bmi_calculator.domain.model.NotificationType
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import java.text.DateFormat
import java.util.*

@Composable
fun NotificationScreen(
    onBackClick: () -> Unit
) {
    // Mock notifications for now as per user's "decide for me what should be there"
    val notifications = remember {
        listOf(
            AppNotification(
                id = "1",
                title = "Weight Check Reminder",
                message = "It's been a week since your last BMI check. Keeping regular records helps track your progress better!",
                date = Date(),
                type = NotificationType.REMINDER
            ),
            AppNotification(
                id = "2",
                title = "Hydration Tip",
                message = "Drinking enough water can help with metabolism and weight management. Aim for 8 glasses today!",
                date = Date(System.currentTimeMillis() - 86400000), // Yesterday
                type = NotificationType.GENERAL
            ),
            AppNotification(
                id = "3",
                title = "Goal Milestone",
                message = "You're only 2kg away from your target weight! Keep up the great work with your healthy habits.",
                date = Date(System.currentTimeMillis() - 172800000), // 2 days ago
                type = NotificationType.ACHIEVEMENT
            ),
            AppNotification(
                id = "4",
                title = "Healthy BMI Range",
                message = "Your recent BMI calculation shows you're in the healthy range. Consistency is key!",
                date = Date(System.currentTimeMillis() - 259200000), // 3 days ago
                type = NotificationType.INSIGHT
            )
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Notifications",
                onBackClick = onBackClick
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                )
            )
        )
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No notifications yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: AppNotification) {
    val icon = when (notification.type) {
        NotificationType.REMINDER -> Icons.Default.Warning
        NotificationType.ACHIEVEMENT -> Icons.Default.CheckCircle
        NotificationType.INSIGHT -> Icons.Default.Lightbulb
        NotificationType.GENERAL -> Icons.Default.Info
    }

    val iconColor = when (notification.type) {
        NotificationType.REMINDER -> Color(0xFFF59E0B)
        NotificationType.ACHIEVEMENT -> Color(0xFF10B981)
        NotificationType.INSIGHT -> Color(0xFF8B5CF6)
        NotificationType.GENERAL -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = DateFormat.getDateInstance(DateFormat.SHORT).format(notification.date),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
