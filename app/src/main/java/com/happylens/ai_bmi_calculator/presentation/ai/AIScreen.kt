package com.happylens.ai_bmi_calculator.presentation.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen

@Composable
fun AIScreen(
    onNavigate: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    // Simulated state for demonstration - in a real app, this would come from a ViewModel
    val isTrackedProfile = false 

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "AI Insights",
                rightContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "BETA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.AI.route,
                onNavigate = onNavigate
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Subtle decorative gradient in top right
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(x = 150.dp, y = (-100).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                (if (isSystemInDarkTheme()) Color(0xFF10B981) else Color(0xFFD1FAE5)).copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (isTrackedProfile) {
                    item {
                        InsightCard(
                            category = "TREND ANALYSIS",
                            title = "Weight trending down",
                            description = "Over the last month Foysal's weight changed by -3.8 kg (-0.8 kg this week). Current BMI is 23.6 — normal.",
                            borderColor = Color(0xFF8B5CF6)
                        )
                    }

                    item {
                        InsightCard(
                            category = "GOAL PROJECTION",
                            title = "Gaining toward 66.0 kg",
                            description = "At the current pace of -0.9 kg/week, Foysal should reach 66.0 kg around July 28. Keep it up!",
                            borderColor = MaterialTheme.colorScheme.primary
                        )
                    }

                    item {
                        InsightCard(
                            category = "SMART SUGGESTION",
                            title = "For the normal range",
                            description = "You're in the healthy range. Keep 150 minutes of moderate activity a week and a protein-forward diet to stay here.",
                            borderColor = Color(0xFF10B981)
                        )
                    }
                } else {
                    item {
                        InsightCard(
                            category = "QUICK CHECK",
                            title = "No tracking on this profile",
                            description = "This profile is for one-off BMI checks. Switch to a tracked profile for trend analysis, projections and personalized coaching.",
                            borderColor = Color(0xFFF97316)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ask the assistant",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AssistantBubble(
                        message = if (isTrackedProfile) {
                            "Hi Foysal! I'm your AI health assistant. Your latest BMI is 23.6 (normal). Ask me anything about your trend, diet or goals."
                        } else {
                            "Hi Others! I'm your AI health assistant. Calculate your BMI and I can analyze it for you. Meanwhile, ask me anything about healthy weight."
                        }
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listOf("How is my trend?", "What should I eat?", "Explain")) { suggestion ->
                            SuggestionChip(text = suggestion)
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(28.dp)),
                            placeholder = { Text("Ask about your health...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { /* Handle send */ },
                            modifier = Modifier
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = Color.White
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
                }
            }
        }
    }
}

@Composable
fun InsightCard(
    category: String,
    title: String,
    description: String,
    borderColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .background(borderColor, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = borderColor.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun AssistantBubble(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun SuggestionChip(text: String) {
    Surface(
        onClick = { /* Handle click */ },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
