package com.happylens.ai_bmi_calculator.presentation.ai

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.happylens.ai_bmi_calculator.domain.model.ChatMessage
import com.happylens.ai_bmi_calculator.domain.model.InsightKind
import com.happylens.ai_bmi_calculator.presentation.components.CommonTopBar
import com.happylens.ai_bmi_calculator.presentation.navigation.BottomNavigationBar
import com.happylens.ai_bmi_calculator.presentation.navigation.Screen
import com.happylens.ai_bmi_calculator.ui.glass.GlassCard
import com.happylens.ai_bmi_calculator.ui.glass.GlassIconButton
import com.happylens.ai_bmi_calculator.ui.glass.GlassLevel
import com.happylens.ai_bmi_calculator.ui.glass.LiquidBackdrop
import com.happylens.ai_bmi_calculator.ui.glass.glassRim
import com.happylens.ai_bmi_calculator.ui.glass.liquidGlass
import com.happylens.ai_bmi_calculator.ui.glass.rememberGlassState
import dev.chrisbanes.haze.HazeState

@Composable
fun AIScreen(
    onNavigate: (String) -> Unit,
    viewModel: AIViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }

    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isAssistantTyping.collectAsState()
    val insights by viewModel.insights.collectAsState()
    val suggestionChips by viewModel.suggestionChips.collectAsState()

    val listState = rememberLazyListState()
    val itemCount = 1 + insights.size + 1 + messages.size + if (isTyping) 1 else 0
    LaunchedEffect(itemCount) {
        if (itemCount > 0) listState.animateScrollToItem(itemCount - 1)
    }

    val hazeState = rememberGlassState()

    fun submitMessage(text: String) {
        if (text.isBlank() || isTyping) return
        viewModel.sendMessage(text)
        messageText = ""
    }

    LiquidBackdrop(hazeState = hazeState) {
    Scaffold(
        topBar = {
            CommonTopBar(
                hazeState = hazeState,
                title = "AI Insights",
                rightContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.glassRim(RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = "BETA",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column {
                AssistantInputBar(
                    hazeState = hazeState,
                    suggestionChips = suggestionChips,
                    messageText = messageText,
                    onMessageChange = { messageText = it },
                    onSuggestionClick = { submitMessage(it) },
                    onSend = { submitMessage(messageText) },
                    enabled = !isTyping
                )
                BottomNavigationBar(
                    currentRoute = Screen.AI.route,
                    onNavigate = onNavigate,
                    hazeState = hazeState
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(insights, key = { it.kind.name + it.title }) { insight ->
                InsightCard(
                    hazeState = hazeState,
                    category = insight.category,
                    title = insight.title,
                    description = insight.description,
                    borderColor = colorForInsight(insight.kind)
                )
            }

            item {
                Text(
                    text = "Ask the assistant",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(messages, key = { it.id }) { message ->
                ChatBubble(message = message, hazeState = hazeState)
            }

            if (isTyping) {
                item(key = "typing-indicator") {
                    TypingIndicatorBubble(hazeState = hazeState)
                }
            }

            item {
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding() + 16.dp))
            }
        }
    }
    }
}

private fun colorForInsight(kind: InsightKind): Color = when (kind) {
    InsightKind.TREND -> Color(0xFF8B5CF6)
    InsightKind.GOAL -> Color(0xFF3B82F6)
    InsightKind.SUGGESTION -> Color(0xFF10B981)
    InsightKind.QUICK_CHECK -> Color(0xFFF97316)
}

@Composable
fun InsightCard(
    hazeState: HazeState,
    category: String,
    title: String,
    description: String,
    borderColor: Color
) {
    GlassCard(
        hazeState = hazeState,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .background(borderColor, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = category,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = borderColor.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, hazeState: HazeState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        val shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = if (message.isFromUser) 20.dp else 4.dp,
            bottomEnd = if (message.isFromUser) 4.dp else 20.dp
        )
        if (message.isFromUser) {
            // Solid brand-color fill for the user's own messages so they read as a
            // distinct, opaque "sent" bubble against the assistant's frosted glass ones.
            Surface(
                modifier = Modifier.widthIn(max = 280.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = shape,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 15.sp,
                    color = Color.White,
                    lineHeight = 22.sp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .liquidGlass(hazeState = hazeState, shape = shape, level = GlassLevel.Regular)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble(hazeState: HazeState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .liquidGlass(
                    hazeState = hazeState,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp),
                    level = GlassLevel.Regular
                )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val transition = rememberInfiniteTransition(label = "typing")
                repeat(3) { index ->
                    val alpha by transition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 600, delayMillis = index * 150, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
                    )
                }
            }
        }
    }
}

@Composable
fun AssistantInputBar(
    hazeState: HazeState,
    suggestionChips: List<String>,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                hazeState = hazeState,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                level = GlassLevel.Regular
            )
    ) {
        if (suggestionChips.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestionChips) { suggestion ->
                    SuggestionChip(text = suggestion, onClick = { onSuggestionClick(suggestion) })
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = messageText,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .liquidGlass(hazeState = hazeState, shape = RoundedCornerShape(28.dp), level = GlassLevel.Thin),
                placeholder = { Text("Ask about your health...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )
            Spacer(modifier = Modifier.width(8.dp))
            GlassIconButton(
                onClick = onSend,
                hazeState = hazeState,
                size = 48.dp,
                level = GlassLevel.Regular,
                enabled = enabled && messageText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Send",
                    tint = if (enabled && messageText.isNotBlank()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.glassRim(RoundedCornerShape(20.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
