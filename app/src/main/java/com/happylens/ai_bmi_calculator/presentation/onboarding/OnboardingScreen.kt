package com.happylens.ai_bmi_calculator.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.ui.glass.GlassButton
import com.happylens.ai_bmi_calculator.ui.glass.GlassCard
import com.happylens.ai_bmi_calculator.ui.glass.GlassSegmentedControl
import com.happylens.ai_bmi_calculator.ui.glass.rememberGlassState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val hazeState = rememberGlassState()

    // AI-vibe animated background
    val infiniteTransition = rememberInfiniteTransition(label = "onboarding_bg")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithContent {
                val radian = Math.toRadians(angle.toDouble())
                val x = Math.cos(radian).toFloat()
                val y = Math.sin(radian).toFloat()
                
                // Expanding the gradient reach to ensure smooth coverage during rotation
                val sizeMax = maxOf(size.width, size.height) * 1.5f
                val start = Offset(size.width / 2 + x * sizeMax, size.height / 2 + y * sizeMax)
                val end = Offset(size.width / 2 - x * sizeMax, size.height / 2 - y * sizeMax)

                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E40AF), // Darker Blue
                            Color(0xFF2563EB), // Blue
                            Color(0xFF3B82F6), // Bright Blue
                            Color(0xFF1E40AF), // Loop back
                        ),
                        start = start,
                        end = end
                    )
                )
                drawContent()
            }
            .sparkle(particleCount = 40)
            .hazeSource(state = hazeState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 40.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> FeaturesPage(hazeState = hazeState)
                    2 -> ProfilePage(
                        hazeState = hazeState,
                        name = uiState.name,
                        age = uiState.age,
                        gender = uiState.gender,
                        onNameChange = viewModel::onNameChanged,
                        onAgeChange = viewModel::onAgeChanged,
                        onGenderChange = viewModel::onGenderChanged
                    )
                }
            }

            // Pager Indicator
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        viewModel.completeOnboarding()
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "Get Started" else "Continue",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            TextButton(
                onClick = {
                    viewModel.skipOnboarding()
                    onFinish()
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Skip", color = Color.White.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(32.dp))
                .aiFlow()
                .sparkle(color = Color.White, particleCount = 15)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFF2563EB)
            )
            // Smaller stars like in the screenshot
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-8).dp, y = (-8).dp),
                tint = Color(0xFF2DD4BF)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "AI BMI Calculator",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Know your body. Track your progress. Get intelligent health guidance.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun FeaturesPage(hazeState: HazeState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Everything you need",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        FeatureItem(
            hazeState = hazeState,
            icon = Icons.Default.BarChart,
            iconColor = Color.White,
            title = "Progress tracking",
            description = "Every calculation saved automatically with interactive charts."
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureItem(
            hazeState = hazeState,
            icon = Icons.Default.AutoAwesome,
            iconColor = Color.Cyan,
            title = "AI health insights",
            description = "Trend analysis, smart suggestions and a personal AI assistant.",
            showSparkle = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        FeatureItem(
            hazeState = hazeState,
            icon = Icons.Default.Group,
            iconColor = Color.White,
            title = "Multiple profiles",
            description = "Family and friends get their own private history and goals."
        )
    }
}

@Composable
fun FeatureItem(
    hazeState: HazeState,
    icon: ImageVector,
    iconColor: Color,
    title: String,
    description: String,
    showSparkle: Boolean = false
) {
    GlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(16.dp),
        tint = Color.White.copy(alpha = 0.05f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.2f))
                    .then(if (showSparkle) Modifier.aiFlow(colors = listOf(iconColor, Color.White, iconColor), durationMillis = 2000).sparkle(color = iconColor.copy(alpha = 0.8f), particleCount = 12) else Modifier),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Text(text = description, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ProfilePage(
    hazeState: HazeState,
    name: String,
    age: Int,
    gender: Gender,
    onNameChange: (String) -> Unit,
    onAgeChange: (Int) -> Unit,
    onGenderChange: (Gender) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create your profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        GlassCard(
            hazeState = hazeState,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(24.dp),
            tint = Color.White.copy(alpha = 0.05f)
        ) {
            Text(
                text = "YOUR NAME",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f)
            )
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = { Text("e.g. John Doe", color = Color.White.copy(alpha = 0.4f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AGE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        IconButton(
                            onClick = { if (age > 1) onAgeChange(age - 1) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            text = age.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.White
                        )
                        IconButton(
                            onClick = { onAgeChange(age + 1) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.weight(1.2f)) {
                    Text(
                        text = "GENDER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    GlassSegmentedControl(
                        options = listOf("Male", "Female"),
                        selectedIndex = if (gender == Gender.MALE) 0 else 1,
                        onSelect = { index ->
                            onGenderChange(if (index == 0) Gender.MALE else Gender.FEMALE)
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }
        }

        Text(
            text = "You can add profiles for family and friends later — each keeps its own private history.",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(top = 24.dp).padding(horizontal = 8.dp),
            lineHeight = 16.sp
        )
    }
}

/**
 * Adds a live "sparkle" effect with shimmering and moving particles.
 */
@Composable
fun Modifier.sparkle(
    color: Color = Color.White,
    particleCount: Int = 25
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    this.drawWithContent {
        drawContent()
        
        val random = java.util.Random(42)
        repeat(particleCount) {
            val seedX = random.nextFloat()
            val seedY = random.nextFloat()
            val speed = 0.1f + random.nextFloat() * 0.4f
            
            // Move particles slightly over time
            val x = ((seedX + progress * speed) % 1f) * size.width
            val y = ((seedY + (1f - progress) * speed * 0.5f) % 1f) * size.height
            
            val radius = random.nextFloat() * 2.dp.toPx()
            
            val particleSeed = random.nextFloat()
            val alphaProgress = (progress + particleSeed) % 1f
            val alpha = if (alphaProgress < 0.5f) alphaProgress * 2f else (1f - alphaProgress) * 2f
            
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(x, y),
                alpha = alpha * 0.6f
            )
        }
    }
}

/**
 * Creates a flowing color effect (AI vibe) that moves across the element.
 */
@Composable
fun Modifier.aiFlow(
    colors: List<Color> = listOf(Color(0xFF1E40AF), Color(0xFF2563EB), Color(0xFF3B82F6), Color(0xFF1E40AF)),
    durationMillis: Int = 4000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "aiFlow")
    val flowProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowProgress"
    )

    this.drawWithContent {
        // Draw the moving gradient as a background/overlay
        val gradientSize = size.width + size.height
        val offset = flowProgress * gradientSize
        
        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(offset - gradientSize, offset - gradientSize),
                end = Offset(offset, offset),
                tileMode = TileMode.Repeated
            )
        )
        drawContent()
    }
}
