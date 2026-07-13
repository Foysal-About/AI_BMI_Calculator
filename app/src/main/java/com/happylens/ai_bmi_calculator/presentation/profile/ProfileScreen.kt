package com.happylens.ai_bmi_calculator.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.glass.GlassButton
import com.happylens.ai_bmi_calculator.ui.glass.GlassCard
import com.happylens.ai_bmi_calculator.ui.glass.GlassIconButton
import com.happylens.ai_bmi_calculator.ui.glass.GlassSegmentedControl
import com.happylens.ai_bmi_calculator.ui.glass.LiquidBackdrop
import com.happylens.ai_bmi_calculator.ui.glass.rememberGlassState
import com.happylens.ai_bmi_calculator.ui.theme.SuccessGreen
import com.happylens.ai_bmi_calculator.ui.theme.WarningAmber
import dev.chrisbanes.haze.HazeState

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val profiles by viewModel.profiles.collectAsState()
    val currentProfileName by viewModel.currentProfileName.collectAsState()
    var showCreateSection by remember { mutableStateOf(false) }
    val hazeState = rememberGlassState()

    LiquidBackdrop(hazeState = hazeState) {
    Scaffold(
        topBar = {
            CommonTopBar(
                hazeState = hazeState,
                title = "Profiles",
                onBackClick = onBackClick,
                rightContent = {
                    GlassIconButton(
                        onClick = { showCreateSection = true },
                        hazeState = hazeState,
                        size = 40.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    GlassIconButton(
                        onClick = onSettingsClick,
                        hazeState = hazeState,
                        size = 40.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Screen.Profile.route,
                onNavigate = onNavigate,
                hazeState = hazeState
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Each tracked profile keeps its own private history and goals. Quick-check profiles calculate without saving.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }

            items(profiles) { profileWithCount ->
                val isSelected = profileWithCount.profile.name == currentProfileName
                ProfileItem(
                    profileWithCount = profileWithCount,
                    isSelected = isSelected,
                    hazeState = hazeState,
                    onSelect = { viewModel.selectProfile(it.name) },
                    onDelete = { viewModel.deleteProfile(it) }
                )
            }

            if (!showCreateSection) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassButton(
                        onClick = { showCreateSection = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Add new profile", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                item {
                    CreateProfileCard(
                        hazeState = hazeState,
                        onCancel = { showCreateSection = false },
                        onCreate = { name, isTracked ->
                            viewModel.createProfile(name, isTracked)
                            showCreateSection = false
                        }
                    )
                }
            }
        }
    }
    }
}

@Composable
fun ProfileItem(
    profileWithCount: ProfileWithCount,
    isSelected: Boolean,
    hazeState: HazeState,
    onSelect: (UserProfile) -> Unit,
    onDelete: (UserProfile) -> Unit
) {
    val profile = profileWithCount.profile
    val color = when (profile.name) {
        "Guest User" -> Color.Gray
        "Family" -> Color(0xFF4CAF50)
        "Alex" -> Color(0xFF8B5CF6)
        else -> MaterialTheme.colorScheme.primary
    }

    GlassCard(
        hazeState = hazeState,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(24.dp),
        onClick = { onSelect(profile) },
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (profile.isTracked) "Tracked" else "Quick check",
                    fontSize = 14.sp,
                    color = if (profile.isTracked) SuccessGreen else WarningAmber,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                if (profile.isTracked) {
                    Text(
                        text = "${profileWithCount.entries} entries",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Don't allow deleting the primary profile "Guest User"
                if (profile.name != "Guest User") {
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassIconButton(
                        onClick = { onDelete(profile) },
                        hazeState = hazeState,
                        size = 24.dp
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
    }
}

@Composable
fun CreateProfileCard(
    hazeState: HazeState,
    onCancel: () -> Unit,
    onCreate: (String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var isTracked by remember { mutableStateOf(true) }

    GlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(24.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Profile name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.25f),
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        GlassSegmentedControl(
            options = listOf("Tracked", "Quick check"),
            selectedIndex = if (isTracked) 0 else 1,
            onSelect = { index -> isTracked = index == 0 },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (isTracked) "Saves every calculation with history, goals and graphs."
                   else "Ideal for one-off checks. Nothing is saved.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Cancel")
            }
            GlassButton(
                onClick = { if (name.isNotBlank()) onCreate(name, isTracked) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
