package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey val phoneNumber: String,
    val name: String,
    val isSynced: Boolean, // true if from system, false if saved manually inside app
    val moodStatus: String, // "Calm", "Highly Excited", "Cortisol-Stressed"
    val moodText: String, // e.g. "Synthesizing beta waves"
    val avatarColorHex: String = "#00FFCC", // Store color hex code for styling
    val bpm: Int = 72, // Telemetric overlay heart rate
    val lastActiveTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey val id: String, // Chat ID (can be contact's phone number, or group name/id)
    val name: String,
    val isGroup: Boolean,
    val unreadCount: Int = 0,
    val lastMessageText: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String,
    val senderName: String,
    val senderPhone: String, // "ME" for user, or phone number
    val text: String,
    val translatedText: String? = null, // Dynamically translated via neural link
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean,
    val isVoiceClip: Boolean = false,
    val voiceDuration: Int = 0, // in seconds if voice clip
    val isAiReply: Boolean = false // true if autonomously sent by the AI Proxy Clone
)

@Entity(tableName = "user_statuses")
data class UserStatus(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val senderName: String,
    val senderPhone: String,
    val mood: String, // "Calm", "Highly Excited", "Cortisol-Stressed"
    val statusText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isMe: Boolean = false,
    val avatarColorHex: String = "#00FFCC"
)
