package com.happylens.ai_bmi_calculator.presentation.calculator

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import java.util.Locale

@Composable
fun CalculatorScreen(
    onBackClick: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    var gender by remember { mutableStateOf(Gender.MALE) }
    var age by remember { mutableIntStateOf(25) }
    var height by remember { mutableFloatStateOf(170f) }
    var weight by remember { mutableFloatStateOf(68f) }
    var heightUnit by remember { mutableStateOf("cm") }
    var weightUnit by remember { mutableStateOf("kg") }
    
    val heightInMeters = remember(height, heightUnit) {
        if (heightUnit == "cm") height / 100f else (height * 2.54f) / 100f
    }

    val bmi = remember(weight, heightInMeters, weightUnit) {
        val weightInKg = if (weightUnit == "kg") weight else weight / 2.20462f
        if (heightInMeters > 0) weightInKg / (heightInMeters * heightInMeters) else 0f
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val (bmiCategory, categoryColor) = remember(bmi, primaryColor) {
        when {
            bmi < 18.5f -> "Underweight" to primaryColor
            bmi < 25f -> "Normal" to Color(0xFF10B981)
            bmi < 30f -> "Overweight" to Color(0xFFF59E0B)
            else -> "Obese" to Color(0xFFEF4444)
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "BMI Calculator",
                onBackClick = onBackClick
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
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Gender and Age Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                // Gender Selector
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(10.dp))
                            .padding(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            GenderButton(
                                text = "Male",
                                isSelected = gender == Gender.MALE,
                                onClick = { gender = Gender.MALE },
                                modifier = Modifier.weight(1f)
                            )
                            GenderButton(
                                text = "Female",
                                isSelected = gender == Gender.FEMALE,
                                onClick = { gender = Gender.FEMALE },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Age Selector
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { if (age > 1) age-- },
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = age.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "AGE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray
                            )
                        }
                        IconButton(
                            onClick = { age++ },
                            modifier = Modifier
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Height Card
            MeasurementCard(
                label = "Height",
                value = height,
                onValueChange = { height = it },
                unitOptions = listOf("cm", "ft/in"),
                currentUnit = heightUnit,
                onUnitChange = { newUnit ->
                    if (newUnit != heightUnit) {
                        height = if (newUnit == "ft/in") {
                            height * 0.393701f
                        } else {
                            height / 0.393701f
                        }
                        heightUnit = newUnit
                    }
                },
                content = {
                    Ruler(
                        value = height,
                        range = if (heightUnit == "cm") 100f..250f else 40f..100f,
                        onValueChange = { height = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Card
            MeasurementCard(
                label = "Weight",
                value = weight,
                onValueChange = { weight = it },
                unitOptions = listOf("kg", "lb"),
                currentUnit = weightUnit,
                onUnitChange = { newUnit ->
                    if (newUnit != weightUnit) {
                        weight = if (newUnit == "lb") {
                            weight * 2.20462f
                        } else {
                            weight / 2.20462f
                        }
                        weightUnit = newUnit
                    }
                },
                content = {
                    Ruler(
                        value = weight,
                        range = if (weightUnit == "kg") 30f..200f else 66f..450f,
                        onValueChange = { weight = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "● YOUR RESULT • LIVE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981),
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BMI Result Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BMIGauge(
                        bmi = bmi,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", bmi),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = categoryColor
                    )
                    Text(
                        text = "BODY MASS INDEX",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        color = categoryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = bmiCategory,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (bmi in 18.5f..24.9f) "Within the healthy range ✓" else "Outside the healthy range",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ideal weight range
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val minIdealKg = 18.5f * (heightInMeters * heightInMeters)
                    val maxIdealKg = 24.9f * (heightInMeters * heightInMeters)
                    val minIdeal = if (weightUnit == "kg") minIdealKg else minIdealKg * 2.20462f
                    val maxIdeal = if (weightUnit == "kg") maxIdealKg else maxIdealKg * 2.20462f
                    
                    Column {
                        Text(text = "Ideal weight range", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f %s – %.1f %s", minIdeal, weightUnit, maxIdeal, weightUnit),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    val isRange = bmi in 18.5f..24.9f
                    Text(
                        text = if (isRange) "You're in range" else "Out of range",
                        color = if (isRange) Color(0xFF10B981) else Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BMI Categories
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "BMI Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    BMICategoryItem("Very severely underweight", "≤ 15.9", MaterialTheme.colorScheme.primary, bmi <= 15.9f)
                    BMICategoryItem("Severely underweight", "16.0 – 16.9", MaterialTheme.colorScheme.primary, bmi in 16.0f..16.9f)
                    BMICategoryItem("Underweight", "17.0 – 18.4", MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), bmi in 17.0f..18.4f)
                    BMICategoryItem("Normal", "18.5 – 24.9", Color(0xFF10B981), bmi in 18.5f..24.9f)
                    BMICategoryItem("Overweight", "25.0 – 29.9", Color(0xFFF59E0B), bmi in 25.0f..29.9f)
                    BMICategoryItem("Obese Class I", "30.0 – 34.9", Color(0xFFEF4444), bmi in 30.0f..34.9f)
                    BMICategoryItem("Obese Class II", "35.0 – 39.9", Color(0xFFB91C1C), bmi in 35.0f..39.9f)
                    BMICategoryItem("Obese Class III", "≥ 40.0", Color(0xFF7F1D1D), bmi >= 40.0f)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Recommendation
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "AI Recommendation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B5CF6))
                        Text(
                            text = when {
                                bmi < 18.5f -> "Consider increasing your calorie intake with nutrient-dense foods. Consult with a nutritionist for a personalized plan."
                                bmi < 25f -> "Great position to be in. Maintain with regular activity and consistent sleep — and keep logging weekly so I can catch any drift early."
                                bmi < 30f -> "A balanced diet and increased physical activity can help you reach a healthier range. Small, consistent changes work best."
                                else -> "It's recommended to consult with a healthcare provider to develop a safe and effective weight management plan."
                            },
                            fontSize = 13.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveBmiRecord(
                        weight = weight,
                        height = height,
                        bmi = bmi,
                        category = bmiCategory,
                        weightUnit = weightUnit,
                        heightUnit = heightUnit
                    )
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "Save to history", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

@Composable
fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color(0xFF1F2937) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MeasurementCard(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    unitOptions: List<String>,
    currentUnit: String,
    onUnitChange: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("") }

    // Synchronize textValue with the Float value. 
    // We update on value changes only if not focused, but we ALWAYS update on unit changes.
    LaunchedEffect(value, currentUnit) {
        val formatted = if (currentUnit == "ft/in") {
            val totalInches = value.toInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "$feet' $inches\""
        } else {
            if (label == "Height") String.format(Locale.getDefault(), "%.0f", value)
            else String.format(Locale.getDefault(), "%.1f", value).replace(',', '.')
        }
        
        // If unit changed or we are not focused, update the text
        if (!isFocused || textValue.isEmpty()) {
            textValue = formatted
        }
    }
    
    // Force update when unit changes specifically
    LaunchedEffect(currentUnit) {
        textValue = if (currentUnit == "ft/in") {
            val totalInches = value.toInt()
            val feet = totalInches / 12
            val inches = totalInches % 12
            "$feet' $inches\""
        } else {
            if (label == "Height") String.format(Locale.getDefault(), "%.0f", value)
            else String.format(Locale.getDefault(), "%.1f", value).replace(',', '.')
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = Color(0xFF1F2937)
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 40.dp) // Leave space for label and units
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BasicTextField(
                        value = textValue,
                        onValueChange = { newValue ->
                            textValue = newValue
                            if (currentUnit == "ft/in") {
                                val regex = Regex("""(\d+)'?\s*(\d+)?""")
                                val match = regex.find(newValue)
                                if (match != null) {
                                    val val1 = match.groupValues[1].toFloatOrNull() ?: 0f
                                    val val2 = match.groupValues[2].toFloatOrNull() ?: 0f
                                    if (newValue.contains("'") || newValue.contains("\"")) {
                                        onValueChange(val1 * 12 + val2)
                                    } else if (val1 > 10) {
                                        onValueChange(val1) // Assume total inches if it's a large number
                                    } else {
                                        onValueChange(val1 * 12 + val2)
                                    }
                                }
                            } else {
                                newValue.replace(',', '.').toFloatOrNull()?.let {
                                    onValueChange(it)
                                }
                            }
                        },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .widthIn(min = 60.dp)
                            .width(IntrinsicSize.Min)
                            .onFocusChanged { isFocused = it.isFocused },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = if (currentUnit == "ft/in") KeyboardType.Text else KeyboardType.Decimal
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                    )

                    if (currentUnit != "ft/in") {
                        Text(
                            text = " $currentUnit",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                        .padding(2.dp)
                ) {
                    unitOptions.forEach { unit ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (currentUnit == unit) Color.White else Color.Transparent)
                                .clickable { 
                                    onUnitChange(unit)
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = unit,
                                fontSize = 11.sp,
                                fontWeight = if (currentUnit == unit) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentUnit == unit) Color.Black else Color.Gray
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun Ruler(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            change.consume()
            val newValue = (value - dragAmount.x / 10f).coerceIn(range)
            onValueChange(newValue)
        }
    }) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val spacing = 10.dp.toPx()

        val startValue = (value - (centerX / spacing)).toInt().coerceAtLeast(range.start.toInt())
        val endValue = (value + (centerX / spacing)).toInt().coerceAtMost(range.endInclusive.toInt())

        for (tickValue in startValue..endValue) {
            val x = centerX + (tickValue - value) * spacing
            val isMajor = tickValue % 10 == 0
            val tickHeight = if (isMajor) 30.dp.toPx() else 15.dp.toPx()

            drawLine(
                color = if (isMajor) Color.Gray else Color.LightGray.copy(alpha = 0.5f),
                start = androidx.compose.ui.geometry.Offset(x, height * 0.4f - tickHeight / 2),
                end = androidx.compose.ui.geometry.Offset(x, height * 0.4f + tickHeight / 2),
                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
            )

            if (isMajor) {
                val textLayoutResult = textMeasurer.measure(
                    text = tickValue.toString(),
                    style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x - textLayoutResult.size.width / 2,
                        height * 0.4f + tickHeight / 2 + 4.dp.toPx()
                    )
                )
            }
        }

        // Center indicator
        drawLine(
            color = primaryColor,
            start = androidx.compose.ui.geometry.Offset(centerX, height * 0.4f - 25.dp.toPx()),
            end = androidx.compose.ui.geometry.Offset(centerX, height * 0.4f + 25.dp.toPx()),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun BMIGauge(bmi: Float, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .aspectRatio(2f), // Wide and shallow for the semi-circle
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            // Radius takes up the full width minus some padding
            val radius = (size.width - strokeWidth) / 2
            // Center the arc at the bottom of the canvas
            val arcCenter = androidx.compose.ui.geometry.Offset(size.width / 2, size.height * 0.9f)
            
            val arcRect = androidx.compose.ui.geometry.Rect(
                center = arcCenter,
                radius = radius
            )
            
            val startAngle = 180f
            
            // Draw categories as segments of a semi-circle
            // Underweight: 180-215 (35°)
            drawArc(
                color = primaryColor,
                startAngle = startAngle,
                sweepAngle = 35f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Normal: 215-305 (90°)
            drawArc(
                color = Color(0xFF10B981),
                startAngle = startAngle + 35f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Overweight: 305-345 (40°)
            drawArc(
                color = Color(0xFFF59E0B),
                startAngle = startAngle + 125f,
                sweepAngle = 40f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Obese: 345-360 (15°)
            drawArc(
                color = Color(0xFFEF4444),
                startAngle = startAngle + 165f,
                sweepAngle = 15f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Pointer calculation
            val angle = when {
                bmi < 18.5 -> startAngle + (bmi / 18.5f).coerceIn(0f, 1f) * 35f
                bmi < 25f -> startAngle + 35f + ((bmi - 18.5f) / (25f - 18.5f)).coerceIn(0f, 1f) * 90f
                bmi < 30f -> startAngle + 125f + ((bmi - 25f) / (30f - 25f)).coerceIn(0f, 1f) * 40f
                else -> startAngle + 165f + ((bmi - 30f) / 10f).coerceIn(0f, 1f) * 15f
            }
            
            val radian = Math.toRadians(angle.toDouble())
            val pointerX = arcCenter.x + radius * Math.cos(radian).toFloat()
            val pointerY = arcCenter.y + radius * Math.sin(radian).toFloat()
            
            // White outer circle for pointer
            drawCircle(
                color = Color.White,
                radius = 9.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(pointerX, pointerY)
            )
            // Colored inner circle for pointer
            drawCircle(
                color = if (bmi < 18.5) primaryColor else if (bmi < 25) Color(0xFF10B981) else if (bmi < 30) Color(0xFFF59E0B) else Color(0xFFEF4444),
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(pointerX, pointerY),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

@Composable
fun BMICategoryItem(label: String, range: String, color: Color, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFFE8F5E9) else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                color = if (isSelected) Color(0xFF10B981) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = range,
                fontSize = 13.sp,
                color = if (isSelected) Color(0xFF10B981) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
