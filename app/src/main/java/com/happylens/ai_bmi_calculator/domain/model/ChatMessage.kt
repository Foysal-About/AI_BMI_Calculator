package com.happylens.ai_bmi_calculator.domain.model

data class ChatMessage(
    val id: String,
    val profileName: String,
    val text: String,
    val isFromUser: Boolean
)
