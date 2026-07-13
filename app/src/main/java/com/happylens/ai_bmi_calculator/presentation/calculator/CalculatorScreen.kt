package com.happylens.ai_bmi_calculator.presentation.calculator

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.ui.glass.GlassCard
import com.happylens.ai_bmi_calculator.ui.glass.GlassIconButton
import com.happylens.ai_bmi_calculator.ui.glass.GlassLevel
import com.happylens.ai_bmi_calculator.ui.glass.GlassSegmentedControl
import com.happylens.ai_bmi_calculator.ui.glass.LiquidBackdrop
import com.happylens.ai_bmi_calculator.ui.glass.liquidGlass
import com.happylens.ai_bmi_calculator.ui.glass.rememberGlassState
import com.happylens.ai_bmi_calculator.ui.theme.ErrorRed
import com.happylens.ai_bmi_calculator.ui.theme.SuccessGreen
import com.happylens.ai_bmi_calculator.ui.theme.WarningAmber
import dev.chrisbanes.haze.HazeState
import java.util.Locale
import kotlin.math.*

private fun formatFeetInches(totalInches: Float): String {
    var feet = floor(totalInches / 12f).toInt()
    var inches = totalInches - feet * 12f
    // Guard against "12.0\"" showing up after rounding to 1 decimal place
    if (inches >= 11.95f) {
        inches = 0f
        feet += 1
    }
    val inchesStr = String.format(Locale.getDefault(), "%.1f", inches).replace(',', '.')
    return "$feet' $inchesStr\""
}

@Composable
fun CalculatorScreen(
    onBackClick: () -> Unit,
    viewModel: CalculatorViewModel = viewModel()
) {
    val gender by viewModel.gender.collectAsState()
    val age by viewModel.age.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val heightUnit by viewModel.heightUnit.collectAsState()
    val weightUnit by viewModel.weightUnit.collectAsState()
    
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
            bmi < 25f -> "Normal" to SuccessGreen
            bmi < 30f -> "Overweight" to WarningAmber
            else -> "Obese" to ErrorRed
        }
    }

    val goalWeight = remember(heightInMeters, weightUnit, bmi) {
        val idealBmi = 22.0f
        val idealKg = idealBmi * (heightInMeters * heightInMeters)
        if (weightUnit == "kg") idealKg else idealKg * 2.20462f
    }

    val handleBack = {
        viewModel.saveBmiRecord(
            weight = weight,
            height = height,
            bmi = bmi,
            category = bmiCategory,
            weightUnit = weightUnit,
            heightUnit = heightUnit,
            goalWeight = goalWeight
        )
        onBackClick()
    }

    BackHandler(onBack = handleBack)

    val hazeState = rememberGlassState()

    LiquidBackdrop(hazeState = hazeState) {
    Scaffold(
        topBar = {
            CommonTopBar(
                hazeState = hazeState,
                title = "BMI Calculator",
                onBackClick = handleBack
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
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
                GlassCard(
                    hazeState = hazeState,
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    GlassSegmentedControl(
                        options = listOf("Male", "Female"),
                        selectedIndex = if (gender == Gender.MALE) 0 else 1,
                        onSelect = { index ->
                            viewModel.updateGender(if (index == 0) Gender.MALE else Gender.FEMALE)
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Age Selector
                GlassCard(
                    hazeState = hazeState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        GlassIconButton(
                            onClick = { if (age > 1) viewModel.updateAge(age - 1) },
                            hazeState = hazeState,
                            size = 32.dp
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "AGE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        GlassIconButton(
                            onClick = { viewModel.updateAge(age + 1) },
                            hazeState = hazeState,
                            size = 32.dp
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
                hazeState = hazeState,
                label = "Height",
                value = height,
                onValueChange = { viewModel.updateHeight(it) },
                unitOptions = listOf("cm", "ft/in"),
                currentUnit = heightUnit,
                onUnitChange = { newUnit ->
                    if (newUnit != heightUnit) {
                        val newHeight = if (newUnit == "ft/in") {
                            height * 0.393701f
                        } else {
                            height / 0.393701f
                        }
                        viewModel.updateHeight(newHeight)
                        viewModel.updateHeightUnit(newUnit)
                    }
                },
                content = {
                    Ruler(
                        value = height,
                        range = if (heightUnit == "cm") 100f..250f else 36f..108f,
                        onValueChange = { viewModel.updateHeight(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        majorStep = if (heightUnit == "cm") 10 else 12,
                        labelFormatter = { tick ->
                            if (heightUnit == "cm") tick.toString() else "${tick / 12}'"
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Card
            MeasurementCard(
                hazeState = hazeState,
                label = "Weight",
                value = weight,
                onValueChange = { viewModel.updateWeight(it) },
                unitOptions = listOf("kg", "lb"),
                currentUnit = weightUnit,
                onUnitChange = { newUnit ->
                    if (newUnit != weightUnit) {
                        val newWeight = if (newUnit == "lb") {
                            weight * 2.20462f
                        } else {
                            weight / 2.20462f
                        }
                        viewModel.updateWeight(newWeight)
                        viewModel.updateWeightUnit(newUnit)
                    }
                },
                content = {
                    Ruler(
                        value = weight,
                        range = if (weightUnit == "kg") 30f..200f else 66f..450f,
                        onValueChange = { viewModel.updateWeight(it) },
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
                color = SuccessGreen,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BMI Result Card
            GlassCard(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                level = GlassLevel.Regular,
                contentPadding = PaddingValues(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ideal weight & Goal Card
            GlassCard(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val minIdealKg = 18.5f * (heightInMeters * heightInMeters)
                        val maxIdealKg = 24.9f * (heightInMeters * heightInMeters)
                        val minIdeal = if (weightUnit == "kg") minIdealKg else minIdealKg * 2.20462f
                        val maxIdeal = if (weightUnit == "kg") maxIdealKg else maxIdealKg * 2.20462f
                        
                        Column {
                            Text(text = "Ideal weight range", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f %s – %.1f %s", minIdeal, weightUnit, maxIdeal, weightUnit),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        val isRange = bmi in 18.5f..24.9f
                        Text(
                            text = if (isRange) "Healthy ✓" else "Out of range",
                            color = if (isRange) SuccessGreen else ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Suggested Goal Weight", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = String.format(Locale.getDefault(), "%.1f %s", goalWeight, weightUnit),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BMI Categories
            GlassCard(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(24.dp)
            ) {
                    Text(text = "BMI Categories", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(16.dp))
                    BMICategoryItem("Very severely underweight", "≤ 15.9", MaterialTheme.colorScheme.primary, bmi <= 15.9f)
                    BMICategoryItem("Severely underweight", "16.0 – 16.9", MaterialTheme.colorScheme.primary, bmi in 16.0f..16.9f)
                    BMICategoryItem("Underweight", "17.0 – 18.4", MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), bmi in 17.0f..18.4f)
                    BMICategoryItem("Normal", "18.5 – 24.9", SuccessGreen, bmi in 18.5f..24.9f)
                    BMICategoryItem("Overweight", "25.0 – 29.9", WarningAmber, bmi in 25.0f..29.9f)
                    BMICategoryItem("Obese Class I", "30.0 – 34.9", ErrorRed, bmi in 30.0f..34.9f)
                    BMICategoryItem("Obese Class II", "35.0 – 39.9", Color(0xFF1E3A8A), bmi in 35.0f..39.9f)
                    BMICategoryItem("Obese Class III", "≥ 40.0", Color(0xFF172554), bmi >= 40.0f)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Recommendation
            GlassCard(
                hazeState = hazeState,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                tint = MaterialTheme.colorScheme.primary,
                level = GlassLevel.UltraThin,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "AI Recommendation", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(
                            text = when {
                                bmi < 18.5f -> "Consider increasing your calorie intake with nutrient-dense foods. Consult with a nutritionist for a personalized plan."
                                bmi < 25f -> "Great position to be in. Maintain with regular activity and consistent sleep — and keep logging weekly so I can catch any drift early."
                                bmi < 30f -> "A balanced diet and increased physical activity can help you reach a healthier range. Small, consistent changes work best."
                                else -> "It's recommended to consult with a healthcare provider to develop a safe and effective weight management plan."
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

@Composable
fun MeasurementCard(
    hazeState: HazeState,
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
            formatFeetInches(value)
        } else {
            if (label == "Height") String.format(Locale.getDefault(), "%.0f", value)
            else String.format(Locale.getDefault(), "%.1f", value).replace(',', '.')
        }

        // If unit changed or we are not focused, update the text
        if (!isFocused || textValue.isEmpty()) {
            textValue = formatted
        }
    }

    GlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 40.dp) // Leave space for label and units
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .liquidGlass(hazeState, RoundedCornerShape(12.dp), GlassLevel.UltraThin)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = textValue,
                            onValueChange = { newValue ->
                                // Filter input to allow only numbers and relevant symbols
                                val filtered = newValue.filter { it.isDigit() || it == '.' || it == ',' || it == '\'' || it == '\"' || it == ' ' }
                                textValue = filtered

                                if (currentUnit == "ft/in") {
                                    // Allow dot, quote or space as separators for feet and inches
                                    val regex = Regex("""(\d+)[.'"\s]*(\d+(?:[.,]\d+)?)?""")
                                    val match = regex.find(filtered)
                                    if (match != null) {
                                        val val1 = match.groupValues[1].toFloatOrNull() ?: 0f
                                        val val2 = match.groupValues[2].replace(',', '.').toFloatOrNull() ?: 0f
                                        if (filtered.contains("'") || filtered.contains("\"") || filtered.contains(".") || filtered.contains(" ")) {
                                            onValueChange(val1 * 12 + val2)
                                        } else if (val1 > 10) {
                                            onValueChange(val1) // Assume total inches if it's a large number
                                        } else {
                                            onValueChange(val1 * 12 + val2)
                                        }
                                    }
                                } else {
                                    filtered.replace(',', '.').toFloatOrNull()?.let {
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
                                .widthIn(min = 90.dp)
                                .width(IntrinsicSize.Min)
                                .onFocusChanged { isFocused = it.isFocused },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                        )

                        if (currentUnit != "ft/in") {
                            Text(
                                text = " $currentUnit",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                GlassSegmentedControl(
                    options = unitOptions,
                    selectedIndex = unitOptions.indexOf(currentUnit).coerceAtLeast(0),
                    onSelect = { index -> onUnitChange(unitOptions[index]) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width((unitOptions.size * 48).dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
    }
}

@Composable
fun Ruler(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    majorStep: Int = 10,
    labelFormatter: (Int) -> String = { it.toString() }
) {
    val textMeasurer = rememberTextMeasurer()
    val primaryColor = MaterialTheme.colorScheme.primary
    val density = LocalDensity.current
    val spacingPx = with(density) { 10.dp.toPx() }

    // Use a derived state for range to ensure smooth updates
    val currentRange by rememberUpdatedState(range)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    val scrollableState = rememberScrollableState { delta ->
        val newValue = (value - delta / spacingPx).coerceIn(currentRange)
        val consumed = (value - newValue) * spacingPx
        currentOnValueChange(newValue)
        consumed
    }

    val labelStyle = TextStyle(
        fontSize = 12.sp, 
        color = MaterialTheme.colorScheme.onSurfaceVariant, 
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
    
    val onSurface = MaterialTheme.colorScheme.onSurface
    val tickColorMajor = onSurface.copy(alpha = 0.6f)
    val tickColorMinor = onSurface.copy(alpha = 0.2f)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ControlIconButton(
            icon = Icons.Default.Remove,
            onClick = { currentOnValueChange((value - 1f).coerceIn(currentRange)) },
            enabled = value > currentRange.start
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .scrollable(scrollableState, Orientation.Horizontal)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val centerX = width / 2
                
                val halfWidthUnits = centerX / spacingPx
                // Use floor for more stable tick generation during scroll
                val startValue = floor(value - halfWidthUnits).toInt().coerceAtLeast(currentRange.start.toInt() - 1)
                val endValue = ceil(value + halfWidthUnits).toInt().coerceAtMost(currentRange.endInclusive.toInt() + 1)

                for (tickValue in startValue..endValue) {
                    val x = centerX + (tickValue - value) * spacingPx
                    
                    if (x < -60 || x > width + 60) continue
                    
                    val isMajor = tickValue % majorStep == 0
                    val tickHeight = if (isMajor) 32.dp.toPx() else 16.dp.toPx()
                    val tickColor = if (isMajor) tickColorMajor else tickColorMinor
                    val strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()

                    drawLine(
                        color = tickColor,
                        start = Offset(x, (height - tickHeight) / 2),
                        end = Offset(x, (height + tickHeight) / 2),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    if (isMajor) {
                        val label = labelFormatter(tickValue)
                        // Optimization: Only measure if needed, or use a fixed size to avoid jumping
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            style = labelStyle,
                            topLeft = Offset(
                                x - 25.dp.toPx(),
                                (height + tickHeight) / 2 + 4.dp.toPx()
                            ),
                            size = Size(50.dp.toPx(), 20.dp.toPx())
                        )
                    }
                }

                // Central indicator (needle) - Drawn last to be on top
                drawLine(
                    color = primaryColor,
                    start = Offset(centerX, (height - 48.dp.toPx()) / 2),
                    end = Offset(centerX, (height + 48.dp.toPx()) / 2),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                // Pointer at top - Draw with fixed geometry to avoid allocations in draw loop
                val pointerSize = 12.dp.toPx()
                val pointerPath = Path().apply {
                    moveTo(centerX, (height - 48.dp.toPx()) / 2)
                    lineTo(centerX - pointerSize / 2, (height - 48.dp.toPx()) / 2 - 8.dp.toPx())
                    lineTo(centerX + pointerSize / 2, (height - 48.dp.toPx()) / 2 - 8.dp.toPx())
                    close()
                }
                drawPath(pointerPath, primaryColor)
            }
        }

        ControlIconButton(
            icon = Icons.Default.Add,
            onClick = { currentOnValueChange((value + 1f).coerceIn(currentRange)) },
            enabled = value < currentRange.endInclusive
        )
    }
}

@Composable
private fun ControlIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val pressed = remember { mutableStateOf(false) }

    LaunchedEffect(pressed.value, enabled) {
        if (pressed.value && enabled) {
            currentOnClick()
            delay(500)
            while (pressed.value && enabled) {
                currentOnClick()
                delay(100)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        pressed.value = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed.value = false
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun BMIGauge(bmi: Float, modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val successColor = SuccessGreen
    val warningColor = WarningAmber
    val errorColor = ErrorRed
    val surfaceColor = MaterialTheme.colorScheme.surface

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
                color = successColor,
                startAngle = startAngle + 35f,
                sweepAngle = 90f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Overweight: 305-345 (40°)
            drawArc(
                color = warningColor,
                startAngle = startAngle + 125f,
                sweepAngle = 40f,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = arcRect.topLeft,
                size = arcRect.size
            )
            
            // Obese: 345-360 (15°)
            drawArc(
                color = errorColor,
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
            
            val radian = angle * PI / 180.0
            val pointerX = arcCenter.x + radius * cos(radian).toFloat()
            val pointerY = arcCenter.y + radius * sin(radian).toFloat()
            
            // Surface-colored outer circle for pointer
            drawCircle(
                color = surfaceColor,
                radius = 9.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(pointerX, pointerY)
            )
            // Colored inner circle for pointer
            drawCircle(
                color = if (bmi < 18.5) primaryColor else if (bmi < 25) successColor else if (bmi < 30) warningColor else errorColor,
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
        color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent,
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
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = range,
                fontSize = 13.sp,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
