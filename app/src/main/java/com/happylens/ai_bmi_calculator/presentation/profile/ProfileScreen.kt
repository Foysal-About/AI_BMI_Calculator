package com.happylens.ai_bmi_calculator.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val selectedProfileName by viewModel.currentProfileName.collectAsState()
    
    val profiles = listOf(
        ProfileItem("Foysal", "Tracked", 8, MaterialTheme.colorScheme.primary),
        ProfileItem("Family", "Tracked", 3, Color(0xFF4CAF50)),
        ProfileItem("Others", "Quick check", null, Color(0xFFF59E0B)),
        ProfileItem("Alex", "Tracked", 0, Color(0xFF8B5CF6))
    )

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Profiles",
                rightContent = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Gray
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Profile.route,
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
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Each tracked profile keeps its own private history and goals. Quick-check profiles calculate without saving.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(profiles) { profile ->
                            ProfileCard(
                                profile = profile,
                                isSelected = profile.name == selectedProfileName,
                                onClick = { 
                                    viewModel.selectProfile(profile.name)
                                    onNavigate(Screen.Home.route)
                                }
                            )
                        }
                        item {
                            AddProfileButton()
                        }
                        item {
                            Spacer(modifier = Modifier.height(padding.calculateBottomPadding() + 24.dp))
                        }
                    }
                }
            }
        }
}

data class ProfileItem(
    val name: String,
    val status: String,
    val entries: Int?,
    val color: Color
)

@Composable
fun ProfileCard(
    profile: ProfileItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(24.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(profile.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = profile.status,
                    fontSize = 14.sp,
                    color = if (profile.status == "Quick check") Color(0xFFF59E0B) else Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
            if (profile.entries != null) {
                Text(
                    text = "${profile.entries} entries",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun AddProfileButton() {
    val stroke = Stroke(width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { }
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.3f),
                style = stroke,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = primaryColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add profile",
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
