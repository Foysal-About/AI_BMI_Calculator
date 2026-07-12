package com.happylens.ai_bmi_calculator.presentation.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import com.happylens.ai_bmi_calculator.ui.theme.ErrorRed
import com.happylens.ai_bmi_calculator.ui.theme.InfoBlue
import com.happylens.ai_bmi_calculator.ui.theme.SuccessGreen
import com.happylens.ai_bmi_calculator.ui.theme.WarningAmber
import java.util.Locale

@Composable
fun ProgressScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProgressViewModel = viewModel(),
) {
    val bmiRecords by viewModel.bmiRecords.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Progress",
                rightContent = {
                    Text(
                        text = "History",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onNavigate(Screen.History.route) }
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Progress.route,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
                Spacer(modifier = Modifier.height(8.dp))

                WeightGoalCard(userProfile, bmiRecords.firstOrNull())

                Spacer(modifier = Modifier.height(24.dp))

                TrendCard(bmiRecords)

                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
            }
    }
}

@Composable
fun WeightGoalCard(
    userProfile: UserProfile?,
    latestRecord: BmiRecord?
) {
    if (latestRecord == null) return

    val currentWeight = latestRecord.weight
    val weightUnit = latestRecord.weightUnit
    
    val currentWeightKg = if (weightUnit == "lb") currentWeight / 2.20462f else currentWeight
    
    val heightInM = if (latestRecord.heightUnit == "in") latestRecord.height * 0.0254f else latestRecord.height / 100f
    val idealMinKg = 18.5f * heightInM * heightInM
    val idealMaxKg = 24.9f * heightInM * heightInM
    val idealAvgKg = (idealMinKg + idealMaxKg) / 2f
    
    val targetWeightKg = if (userProfile?.targetWeight != null && userProfile.targetWeight > 0f) {
        userProfile.targetWeight
    } else {
        idealAvgKg
    }

    val startingWeightKg = if (userProfile?.startingWeight != null && userProfile.startingWeight > 0f) {
        userProfile.startingWeight
    } else {
        currentWeightKg
    }

    val isGain = targetWeightKg > startingWeightKg
    val label = if (isGain) "Gain to" else "Lose to"
    
    val totalChangeNeeded = targetWeightKg - startingWeightKg
    val progress = if (totalChangeNeeded == 0f) 1f else {
        ((currentWeightKg - startingWeightKg) / totalChangeNeeded).coerceIn(0f, 1f)
    }

    val displayUnit = weightUnit
    val targetDisplay = if (displayUnit == "lb") targetWeightKg * 2.20462f else targetWeightKg
    val currentDisplay = if (displayUnit == "lb") currentWeightKg * 2.20462f else currentWeightKg
    val idealMinDisplay = if (displayUnit == "lb") idealMinKg * 2.20462f else idealMinKg
    val idealMaxDisplay = if (displayUnit == "lb") idealMaxKg * 2.20462f else idealMaxKg

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = Color(0xFF9b6df6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$label ${String.format(Locale.getDefault(), "%.1f", targetDisplay)} $displayUnit",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9b6df6).copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF9b6df6),
                                    Color(0xFF2ec4b6)
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
                    text = "Now ${String.format(Locale.getDefault(), "%.1f", currentDisplay)} $displayUnit",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Target ${String.format(Locale.getDefault(), "%.1f", targetDisplay)} $displayUnit",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "Suggested Ideal Weight: %.1f - %.1f %s",
                        idealMinDisplay, idealMaxDisplay, displayUnit
                    ),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TrendCard(bmiRecords: List<BmiRecord>) {
    var selectedType by remember { mutableStateOf("Weight") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedType == "Weight") "Weight Trend" else "BMI Trend",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    TrendTypeOption("Weight", selectedType == "Weight") { selectedType = "Weight" }
                    TrendTypeOption("BMI", selectedType == "BMI") { selectedType = "BMI" }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (bmiRecords.size < 2) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add more records to see trend",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            } else {
                val values = remember(bmiRecords, selectedType) {
                    bmiRecords.take(7).reversed().map { record ->
                        if (selectedType == "Weight") {
                            if (record.weightUnit == "lb") record.weight / 2.20462f else record.weight
                        } else {
                            record.bmi
                        }
                    }
                }
                
                val chartColor = if (selectedType == "Weight") InfoBlue else MaterialTheme.colorScheme.primary
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val minValue = values.minOrNull() ?: 0f
                    val maxValue = values.maxOrNull() ?: 100f
                    val valueRange = (maxValue - minValue).coerceAtLeast(1f)
                    val canvasPadding = valueRange * 0.15f
                    
                    val plotMin = minValue - canvasPadding
                    val plotMax = maxValue + canvasPadding
                    val plotRange = plotMax - plotMin
                    
                    val stepX = size.width / (values.size - 1)
                    val path = Path()
                    
                    values.forEachIndexed { index, value ->
                        val x = index * stepX
                        val y = size.height - ((value - plotMin) / plotRange * size.height)
                        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    
                    drawPath(
                        path = path,
                        color = chartColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    
                    // Add dots
                    values.forEachIndexed { index, value ->
                        val x = index * stepX
                        val y = size.height - ((value - plotMin) / plotRange * size.height)
                        drawCircle(
                            color = chartColor,
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

                if (selectedType == "BMI") {
                    Spacer(modifier = Modifier.height(20.dp))
                    val maxBmi = values.maxOrNull() ?: 0f
                    val minBmi = values.minOrNull() ?: 0f
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TrendStatCard("High", String.format(Locale.getDefault(), "%.1f", maxBmi), getBmiColor(maxBmi), modifier = Modifier.weight(1f))
                        TrendStatCard("Low", String.format(Locale.getDefault(), "%.1f", minBmi), getBmiColor(minBmi), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun TrendTypeOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TrendStatCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = valueColor.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, valueColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

private fun getBmiColor(bmi: Float): Color {
    return when {
        bmi < 18.5f -> InfoBlue
        bmi < 25f -> SuccessGreen
        bmi < 30f -> WarningAmber
        else -> ErrorRed
    }
}
