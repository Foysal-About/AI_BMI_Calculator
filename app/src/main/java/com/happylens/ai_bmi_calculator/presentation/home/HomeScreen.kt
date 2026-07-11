package com.happylens.ai_bmi_calculator.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar

import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import androidx.compose.runtime.remember
import java.util.Locale
import java.util.Date
import java.util.Calendar
import java.text.DateFormat

private fun greetingForCurrentTime(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
fun HomeScreen(
    onCalculateClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val bmiRecords by viewModel.bmiRecords.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val targetWeight by viewModel.targetWeight.collectAsState()
    val startingWeight by viewModel.startingWeight.collectAsState()
    val latestRecord = bmiRecords.firstOrNull()
    val greeting = remember { greetingForCurrentTime() }

    var showProfileDropdown by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }
    val profiles = listOf("Foysal", "Family", "Alex", "Others")

    if (showTargetDialog) {
        TargetWeightDialog(
            initialWeight = targetWeight,
            onDismiss = { showTargetDialog = false },
            onConfirm = {
                viewModel.updateTargetWeight(it)
                showTargetDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                titleContent = {
                    Box {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showProfileDropdown = true }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = greeting,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profileName,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(24.dp).padding(top = 4.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showProfileDropdown,
                            onDismissRequest = { showProfileDropdown = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                .width(200.dp)
                        ) {
                            profiles.forEach { name ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = name,
                                            fontWeight = if (name == profileName) FontWeight.Bold else FontWeight.Normal,
                                            color = if (name == profileName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        ) 
                                    },
                                    onClick = {
                                        viewModel.selectProfile(name)
                                        showProfileDropdown = false
                                    },
                                    leadingIcon = {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (name == "Foysal") MaterialTheme.colorScheme.primary 
                                                    else if (name == "Family") Color(0xFF4CAF50)
                                                    else if (name == "Alex") Color(0xFF8B5CF6)
                                                    else Color(0xFFF59E0B)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.take(1),
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            DropdownMenuItem(
                                text = { Text("Manage Profiles") },
                                onClick = {
                                    showProfileDropdown = false
                                    onNavigate(Screen.Profile.route)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            )
                        }
                    }
                },
                rightContent = {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onNavigate(Screen.Profile.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profileName.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Home.route,
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                if (latestRecord == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No BMI yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Run your first calculation to see your health snapshot here.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Simple Circle BMI display
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { (latestRecord.bmi / 40f).coerceIn(0f, 1f) },
                                    modifier = Modifier.fillMaxSize(),
                                    color = when {
                                        latestRecord.bmi < 18.5f -> Color(0xFF3B82F6)
                                        latestRecord.bmi < 25f -> Color(0xFF10B981)
                                        latestRecord.bmi < 30f -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    strokeWidth = 8.dp,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f", latestRecord.bmi),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(text = "BMI", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.width(24.dp))

                            Column {
                                Surface(
                                    color = (when {
                                        latestRecord.bmi < 18.5f -> Color(0xFF3B82F6)
                                        latestRecord.bmi < 25f -> Color(0xFF10B981)
                                        latestRecord.bmi < 30f -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    }).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = latestRecord.category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = when {
                                            latestRecord.bmi < 18.5f -> Color(0xFF3B82F6)
                                            latestRecord.bmi < 25f -> Color(0xFF10B981)
                                            latestRecord.bmi < 30f -> Color(0xFFF59E0B)
                                            else -> Color(0xFFEF4444)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val weeklyDiffText = if (bmiRecords.size > 1) {
                                    val diff = latestRecord.weight - bmiRecords[1].weight
                                    val sign = if (diff > 0) "+" else ""
                                    " • $sign${String.format(Locale.getDefault(), "%.1f", diff)} kg this week"
                                } else ""
                                Text(
                                    text = "${latestRecord.weight} ${latestRecord.weightUnit} • ${latestRecord.height.toInt()} ${latestRecord.heightUnit}$weeklyDiffText",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Updated ${DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(latestRecord.date)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onCalculateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calculate BMI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (latestRecord != null) {
                item {
                    GoalProgressCard(
                        currentWeight = latestRecord.weight,
                        targetWeight = targetWeight,
                        startingWeight = startingWeight ?: latestRecord.weight,
                        onClick = { showTargetDialog = true }
                    )
                }
            }

            if (bmiRecords.isNotEmpty()) {
                item {
                    val currentWeight = latestRecord?.weight ?: 0f
                    val startingWeightVal = startingWeight ?: currentWeight
                    val weeklyDiff = if (bmiRecords.size > 1)
                        String.format(Locale.getDefault(), "%.1f kg", currentWeight - bmiRecords[1].weight)
                    else "0.0 kg"

                    val totalDiff = String.format(Locale.getDefault(), "%.1f kg", currentWeight - startingWeightVal)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryStatCard("Weekly", weeklyDiff, Color(0xFF10B981), modifier = Modifier.weight(1f))
                        SummaryStatCard("Total", totalDiff, Color(0xFF10B981), modifier = Modifier.weight(1f))
                    }
                }

                item {
                    TrendCard()
                }

                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Recent", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            TextButton(onClick = { onNavigate(Screen.History.route) }) {
                                Text(text = "See all", color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                bmiRecords.take(3).forEachIndexed { index, record ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        record.bmi < 18.5f -> Color(0xFF3B82F6)
                                                        record.bmi < 25f -> Color(0xFF10B981)
                                                        record.bmi < 30f -> Color(0xFFF59E0B)
                                                        else -> Color(0xFFEF4444)
                                                    }
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(record.date),
                                            modifier = Modifier.weight(1f),
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${record.weight} ${record.weightUnit}",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.1f", record.bmi),
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                record.bmi < 18.5f -> Color(0xFF3B82F6)
                                                record.bmi < 25f -> Color(0xFF10B981)
                                                record.bmi < 30f -> Color(0xFFF59E0B)
                                                else -> Color(0xFFEF4444)
                                            }
                                        )
                                    }
                                    if (index < bmiRecords.take(3).size - 1) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AI Insight",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (latestRecord == null)
                                    "Run your first calculation — tap for details"
                                else "Weight trending ${if (bmiRecords.size > 1 && bmiRecords[0].weight < bmiRecords[1].weight) "down" else "stable"} — tap for details",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TargetWeightDialog(
    initialWeight: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var weight by remember { mutableStateOf(initialWeight) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Set Goal Weight") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f kg", weight),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = weight,
                    onValueChange = { weight = it },
                    valueRange = 30f..150f,
                    steps = 1200 // 0.1 increments roughly
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(weight) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SummaryStatCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun TrendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Weight Trend",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            // Simple Chart Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.2f)
                        cubicTo(
                            size.width * 0.3f, size.height * 0.1f,
                            size.width * 0.6f, size.height * 0.4f,
                            size.width, size.height * 0.3f
                        )
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF3B82F6),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun GoalProgressCard(
    currentWeight: Float,
    targetWeight: Float,
    startingWeight: Float,
    onClick: () -> Unit
) {
    val diff = targetWeight - currentWeight
    val label = when {
        diff < -0.05f -> "Lose to"
        diff > 0.05f -> "Gain to"
        else -> "Maintain"
    }

    val progress = remember(currentWeight, targetWeight, startingWeight) {
        val total = startingWeight - targetWeight
        if (total == 0f) 1f else ((startingWeight - currentWeight) / total).coerceIn(0f, 1f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$label ${String.format(Locale.getDefault(), "%.1f", targetWeight)} kg",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFF10B981)
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Now ${String.format(Locale.getDefault(), "%.1f", currentWeight)} kg",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Target ${String.format(Locale.getDefault(), "%.1f", targetWeight)} kg",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
