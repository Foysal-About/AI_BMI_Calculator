package com.happylens.ai_bmi_calculator.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.ChatMessage
import com.happylens.ai_bmi_calculator.domain.model.Insight
import com.happylens.ai_bmi_calculator.domain.model.InsightKind
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class AIViewModel : ViewModel() {
    private val repository = BmiRepository

    val messages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAssistantTyping = MutableStateFlow(false)
    val isAssistantTyping: StateFlow<Boolean> = _isAssistantTyping.asStateFlow()

    private val _insights = MutableStateFlow<List<Insight>>(emptyList())
    val insights: StateFlow<List<Insight>> = _insights.asStateFlow()

    private val _suggestionChips = MutableStateFlow(
        listOf("How's my BMI trend?", "Tips for weight loss", "What's my goal status?", "Check progress")
    )
    val suggestionChips: StateFlow<List<String>> = _suggestionChips.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        _insights.value = listOf(
            Insight(
                title = "Weight Trend",
                description = "You've been consistently tracking your weight. Keep it up!",
                category = "Consistency",
                kind = InsightKind.TREND
            ),
            Insight(
                title = "Daily Goal",
                description = "Try to drink more water today to help with your metabolism.",
                category = "Hydration",
                kind = InsightKind.GOAL
            )
        )
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            val profileName = repository.currentProfileName.first()
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                profileName = profileName,
                text = text,
                isFromUser = true
            )
            repository.addChatMessage(userMessage)

            _isAssistantTyping.value = true
            delay(1500) // Simulate AI thinking
            
            val assistantMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                profileName = profileName,
                text = "I've received your message: \"$text\". I'm here to help you with your health goals!",
                isFromUser = false
            )
            repository.addChatMessage(assistantMessage)
            _isAssistantTyping.value = false
        }
    }
}
