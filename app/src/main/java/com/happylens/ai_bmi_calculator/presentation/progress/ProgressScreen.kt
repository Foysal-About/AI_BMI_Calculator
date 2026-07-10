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
import java.util.Locale

@Composable
fun ProgressScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProgressViewModel = viewModel(),
) {
    val currentWeight by viewModel.currentWeight.collectAsState()
    val targetWeight by viewModel.targetWeight.collectAsState()
    val startingWeight by viewModel.startingWeight.collectAsState()

    var showTargetDialog by remember { mutableStateOf(value = false) }

    if (showTargetDialog) {
        TargetWeightDialog(
            initialWeight = targetWeight,
            onDismiss = { showTargetDialog = false },
        ) {
            viewModel.updateTargetWeight(it)
            showTargetDialog = false
        }
    }

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
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.White,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                WeightGoalCard(
                    currentWeight = currentWeight ?: 0f,
                    targetWeight = targetWeight,
                    startingWeight = startingWeight ?: currentWeight ?: 0f,
                    onEditTarget = { showTargetDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                val weeklyDiff = if ((currentWeight != null) && (startingWeight != null)) 
                    String.format(Locale.getDefault(), "%.1f kg", currentWeight!! - startingWeight!!)
                    else "-0.0 kg"
                
                val totalDiff = if ((currentWeight != null) && (startingWeight != null))
                    String.format(Locale.getDefault(), "%.1f kg", currentWeight!! - startingWeight!!)
                    else "-0.0 kg"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SummaryStatCard("Weekly", weeklyDiff, Color(0xFF10B981), modifier = Modifier.weight(1f))
                    SummaryStatCard("Total", totalDiff, Color(0xFF10B981), modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                TrendCard()

                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 24.dp))
            }
        }
    }
}

@Composable
fun WeightGoalCard(
    currentWeight: Float,
    targetWeight: Float,
    startingWeight: Float,
    onEditTarget: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onEditTarget
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Current Weight",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f kg", currentWeight),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Goal",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f kg", targetWeight),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Bar
            val progress = remember(currentWeight, targetWeight, startingWeight) {
                if (startingWeight == targetWeight) 0f
                else {
                    val totalToLose = startingWeight - targetWeight
                    val lostSoFar = startingWeight - currentWeight
                    if (totalToLose == 0f) 0f else (lostSoFar / totalToLose).coerceIn(0f, 1f)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F4F6))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f kg", startingWeight),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                
                val toGo = currentWeight - targetWeight
                Text(
                    text = if (toGo > 0) String.format(Locale.getDefault(), "%.1f kg to go", toGo) else "Goal reached!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = String.format(Locale.getDefault(), "%.1f kg", targetWeight),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Weight Trend",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
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
