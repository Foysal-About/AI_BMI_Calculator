package com.happylens.ai_bmi_calculator.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar

import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import androidx.compose.runtime.remember
import java.util.Locale
import java.util.Date
import java.text.DateFormat

@Composable
fun HomeScreen(
    onCalculateClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val bmiRecords by viewModel.bmiRecords.collectAsState()
    val profileName by viewModel.profileName.collectAsState()
    val latestRecord = bmiRecords.firstOrNull()
    
    var showProfileDropdown by remember { mutableStateOf(false) }
    val profiles = listOf("Foysal", "Family", "Alex", "Others")

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
                                text = "Good afternoon",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profileName,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(24.dp).padding(top = 4.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showProfileDropdown,
                            onDismissRequest = { showProfileDropdown = false },
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .width(200.dp)
                        ) {
                            profiles.forEach { name ->
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            text = name,
                                            fontWeight = if (name == profileName) FontWeight.Bold else FontWeight.Normal,
                                            color = if (name == profileName) MaterialTheme.colorScheme.primary else Color.Black
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
        containerColor = Color.Transparent
    ) { padding ->
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
                    .padding(top = padding.calculateTopPadding())
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                    if (latestRecord == null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Run your first calculation to see your health snapshot here.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                        trackColor = Color(0xFFF3F4F6),
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = String.format(Locale.getDefault(), "%.1f", latestRecord.bmi),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = "BMI", fontSize = 10.sp, color = Color.Gray)
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
                                    Text(
                                        text = "${latestRecord.weight} ${latestRecord.weightUnit} • ${latestRecord.height.toInt()} ${latestRecord.heightUnit}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Updated ${DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(latestRecord.date)}",
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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

                    if (bmiRecords.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Recent", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            TextButton(onClick = { /* See all */ }) {
                                Text(text = "See all", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "${record.weight} ${record.weightUnit}",
                                            color = Color.Gray,
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
                                        HorizontalDivider(color = Color(0xFFF3F4F6))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
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
                                    .background(Color(0xFF8B5CF6).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF8B5CF6),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Insight",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B5CF6)
                                )
                                Text(
                                    text = if (latestRecord == null) 
                                        "Run your first calculation — tap for details" 
                                        else "Weight trending ${if (bmiRecords.size > 1 && bmiRecords[0].weight < bmiRecords[1].weight) "down" else "stable"} — tap for details",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Added spacer to ensure content is scrollable behind the floating glass bar
                    Spacer(modifier = Modifier.height(padding.calculateBottomPadding() + 24.dp))
                }
            }
        }
}
