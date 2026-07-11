package com.happylens.ai_bmi_calculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.happylens.ai_bmi_calculator.domain.model.ChatMessage

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val profileName: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    profileName = profileName,
    text = text,
    isFromUser = isFromUser
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    profileName = profileName,
    text = text,
    isFromUser = isFromUser
)
