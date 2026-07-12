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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import kotlin.math.abs
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar

import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.theme.ErrorRed
import com.happylens.ai_bmi_calculator.ui.theme.InfoBlue
import com.happylens.ai_bmi_calculator.ui.theme.SuccessGreen
import com.happylens.ai_bmi_calculator.ui.theme.WarningAmber
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
    val allProfiles by viewModel.allProfiles.collectAsState()
    val targetWeight by viewModel.targetWeight.collectAsState()
    val startingWeight by viewModel.startingWeight.collectAsState()
    val isTracked by viewModel.isTracked.collectAsState()
    val latestRecord = bmiRecords.firstOrNull()
    val greeting = remember { greetingForCurrentTime() }

    var showProfileDropdown by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(false) }

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
                modifier = Modifier.padding(horizontal = 8.dp),
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
                                    modifier = Modifier.size(24.dp)
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
                            allProfiles.forEach { profile ->
                                val name = profile.name
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
                                                    if (name == "Guest User") Color.Gray
                                                    else if (name == "Family") Color(0xFF4CAF50)
                                                    else if (name == "Alex") Color(0xFF8B5CF6)
                                                    else MaterialTheme.colorScheme.primary
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = name.take(1).uppercase(),
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
                    IconButton(
                        onClick = { onNavigate(Screen.Notification.route) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = ErrorRed,
                                    contentColor = Color.White,
                                ) {
                                    Text("4")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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
                                        latestRecord.bmi < 18.5f -> InfoBlue
                                        latestRecord.bmi < 25f -> SuccessGreen
                                        latestRecord.bmi < 30f -> WarningAmber
                                        else -> ErrorRed
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
                                        latestRecord.bmi < 18.5f -> InfoBlue
                                        latestRecord.bmi < 25f -> SuccessGreen
                                        latestRecord.bmi < 30f -> WarningAmber
                                        else -> ErrorRed
                                    }).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = latestRecord.category,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = when {
                                            latestRecord.bmi < 18.5f -> InfoBlue
                                            latestRecord.bmi < 25f -> SuccessGreen
                                            latestRecord.bmi < 30f -> WarningAmber
                                            else -> ErrorRed
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val weeklyDiffText = if (bmiRecords.size > 1 && isTracked) {
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

            if (latestRecord != null && isTracked) {
                item {
                    val currentKg = if (latestRecord.weightUnit == "lb") latestRecord.weight / 2.20462f else latestRecord.weight
                    GoalProgressCard(
                        currentWeight = currentKg,
                        targetWeight = targetWeight,
                        startingWeight = startingWeight ?: currentKg,
                        displayUnit = latestRecord.weightUnit,
                        onClick = { showTargetDialog = true }
                    )
                }
            }

            if (bmiRecords.isNotEmpty() && isTracked) {
                item {
                    val latest = bmiRecords[0]
                    val currentKg = if (latest.weightUnit == "lb") latest.weight / 2.20462f else latest.weight
                    val startKg = startingWeight ?: currentKg
                    val prevKg = if (bmiRecords.size > 1) {
                        val prev = bmiRecords[1]
                        if (prev.weightUnit == "lb") prev.weight / 2.20462f else prev.weight
                    } else currentKg

                    val weeklyDiffKg = currentKg - prevKg
                    val totalDiffKg = currentKg - startKg
                    
                    val unit = latest.weightUnit
                    val weeklyDiff = if (unit == "lb") weeklyDiffKg * 2.20462f else weeklyDiffKg
                    val totalDiff = if (unit == "lb") totalDiffKg * 2.20462f else totalDiffKg

                    val weeklyText = String.format(Locale.getDefault(), "%+1.1f %s", weeklyDiff, unit)
                    val totalText = String.format(Locale.getDefault(), "%+1.1f %s", totalDiff, unit)
                    
                    val goalIsLoss = targetWeight < startKg
                    
                    val weeklyColor = when {
                        weeklyDiffKg < -0.1f -> SuccessGreen
                        weeklyDiffKg > 0.1f -> ErrorRed
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                                      
                    val totalColor = when {
                        abs(totalDiffKg) < 0.1f -> MaterialTheme.colorScheme.onSurfaceVariant
                        goalIsLoss -> if (totalDiffKg < 0) SuccessGreen else ErrorRed
                        else -> if (totalDiffKg > 0) SuccessGreen else ErrorRed
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryStatCard("Weekly", weeklyText, weeklyColor, modifier = Modifier.weight(1f))
                        SummaryStatCard("Total", totalText, totalColor, modifier = Modifier.weight(1f))
                    }
                }

                item {
                    TrendCard(bmiRecords)
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
                                                        record.bmi < 18.5f -> InfoBlue
                                                        record.bmi < 25f -> SuccessGreen
                                                        record.bmi < 30f -> WarningAmber
                                                        else -> ErrorRed
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
                                                record.bmi < 18.5f -> InfoBlue
                                                record.bmi < 25f -> SuccessGreen
                                                record.bmi < 30f -> WarningAmber
                                                else -> ErrorRed
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

            if (isTracked || bmiRecords.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        onClick = { onNavigate(Screen.AI.route) }
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
                                val insightText = remember(bmiRecords) {
                                    if (latestRecord == null) {
                                        "Run your first calculation — tap for details"
                                    } else {
                                        val trend = if (bmiRecords.size > 1) {
                                            val diff = (if (bmiRecords[0].weightUnit == "lb") bmiRecords[0].weight / 2.20462f else bmiRecords[0].weight) - 
                                                       (if (bmiRecords[1].weightUnit == "lb") bmiRecords[1].weight / 2.20462f else bmiRecords[1].weight)
                                            when {
                                                diff < -0.1f -> "down"
                                                diff > 0.1f -> "up"
                                                else -> "stable"
                                            }
                                        } else "stable"
                                        "Weight trending $trend — tap for details"
                                    }
                                }
                                Text(
                                    text = insightText,
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
}

@Composable
fun TargetWeightDialog(
    initialWeight: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var weight by remember { mutableFloatStateOf(initialWeight) }

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
fun TrendCard(bmiRecords: List<BmiRecord>) {
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
            
            if (bmiRecords.size < 2) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add more records to see trend",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                val weights = remember(bmiRecords) {
                    bmiRecords.take(7).reversed().map { record ->
                        if (record.weightUnit == "lb") record.weight / 2.20462f else record.weight
                    }
                }
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val minWeight = weights.minOrNull() ?: 0f
                    val maxWeight = weights.maxOrNull() ?: 100f
                    val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)
                    val canvasPadding = weightRange * 0.15f
                    
                    val plotMin = minWeight - canvasPadding
                    val plotMax = maxWeight + canvasPadding
                    val plotRange = plotMax - plotMin
                    
                    val stepX = size.width / (weights.size - 1)
                    val path = Path()
                    
                    weights.forEachIndexed { index, weight ->
                        val x = index * stepX
                        val y = size.height - ((weight - plotMin) / plotRange * size.height)
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    
                    drawPath(
                        path = path,
                        color = InfoBlue,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    
                    // Add dots
                    weights.forEachIndexed { index, weight ->
                        val x = index * stepX
                        val y = size.height - ((weight - plotMin) / plotRange * size.height)
                        drawCircle(
                            color = InfoBlue,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
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
    displayUnit: String,
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
        if (abs(total) < 0.1f) 1f else ((startingWeight - currentWeight) / total).coerceIn(0f, 1f)
    }

    // Convert display values
    val currentDisplay = if (displayUnit == "lb") currentWeight * 2.20462f else currentWeight
    val targetDisplay = if (displayUnit == "lb") targetWeight * 2.20462f else targetWeight

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
                    text = "$label ${String.format(Locale.getDefault(), "%.1f %s", targetDisplay, displayUnit)}",
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
                                    SuccessGreen
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
                    text = "Now ${String.format(Locale.getDefault(), "%.1f %s", currentDisplay, displayUnit)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Target ${String.format(Locale.getDefault(), "%.1f %s", targetDisplay, displayUnit)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
