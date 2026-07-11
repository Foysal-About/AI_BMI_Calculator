package com.happylens.ai_bmi_calculator.presentation.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.theme.AI_BMI_CalculatorTheme
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ProgressViewModel = viewModel()
) {
    val bmiRecords by viewModel.bmiRecords.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "History",
                onBackClick = onBackClick,
                rightContent = {
                    Text(
                        text = "${bmiRecords.size} entries",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(bmiRecords.indices.toList()) { index ->
                    val record = bmiRecords[index]
                    val weightChange = if (index < bmiRecords.size - 1) {
                        val diff = record.weight - bmiRecords[index + 1].weight
                        val sign = if (diff > 0) "+" else ""
                        String.format(Locale.getDefault(), "%s%.1f kg", sign, diff)
                    } else {
                        "—"
                    }
                    HistoryItem(
                        record = record,
                        weightChange = weightChange,
                        onDelete = { viewModel.deleteRecord(record.id) }
                    )
                }
            }
    }
}

@Composable
fun HistoryItem(record: BmiRecord, weightChange: String, onDelete: () -> Unit) {
    val statusColor = when {
        record.bmi < 18.5f -> MaterialTheme.colorScheme.primary
        record.bmi < 25f -> Color(0xFF10B981)
        record.bmi < 30f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }
    val statusBgColor = statusColor.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BMI Badge
            Column(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(statusBgColor),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", record.bmi),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Text(
                    text = "BMI",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Date and Status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = SimpleDateFormat("MMM d", Locale.ENGLISH).format(record.date),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = record.category,
                    fontSize = 14.sp,
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Weight and Change
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f kg", record.weight),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = weightChange,
                    fontSize = 14.sp,
                    color = if (weightChange.startsWith("-")) Color(0xFF10B981) else if (weightChange.startsWith("+")) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Delete Button
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    AI_BMI_CalculatorTheme {
        HistoryScreen(onBackClick = {}, onNavigate = {})
    }
}
