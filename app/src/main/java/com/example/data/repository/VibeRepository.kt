package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ContactDao
import com.example.data.local.MessageDao
import com.example.data.local.UserStatusDao
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.UserStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class VibeRepository(
    private val contactDao: ContactDao,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val userStatusDao: UserStatusDao
) {
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()
    val allChats: Flow<List<Chat>> = chatDao.getAllChats()
    val allStatuses: Flow<List<UserStatus>> = userStatusDao.getAllStatuses()

    fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts("%$query%")
    }

    fun getMessagesForChat(chatId: String): Flow<List<Message>> {
        return messageDao.getMessagesForChat(chatId)
    }

    suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    suspend fun insertChat(chat: Chat) {
        chatDao.insertChat(chat)
    }

    suspend fun insertMessage(message: Message) {
        messageDao.insertMessage(message)
        // Also update the chat's last message details
        chatDao.updateLastMessage(
            chatId = message.chatId,
            text = if (message.isVoiceClip) "🎤 Holographic Media Clip (${message.voiceDuration}s)" else message.text,
            time = message.timestamp,
            unreadIncrement = if (message.isFromMe) 0 else 1
        )
    }

    suspend fun markChatAsRead(chatId: String) {
        chatDao.markChatAsRead(chatId)
    }

    suspend fun insertStatus(status: UserStatus) {
        userStatusDao.insertStatus(status)
    }

    suspend fun updateTranslation(messageId: Int, translatedText: String) {
        messageDao.updateTranslation(messageId, translatedText)
    }

    suspend fun clearMessages(chatId: String) {
        messageDao.clearChatMessages(chatId)
    }

    // Initialize mock data to simulate phone book sync & standard chats
    suspend fun initializeMockDataIfNeeded() {
        val contactsList = contactDao.getAllContacts().first()
        if (contactsList.isEmpty()) {
            val mockContacts = listOf(
                Contact(
                    phoneNumber = "+1 (2040) 111-2222",
                    name = "Nova Prime",
                    isSynced = true,
                    moodStatus = "Highly Excited",
                    moodText = "Hyper-tunnel hyper-drive aligned!",
                    avatarColorHex = "#FF007F", // Neon Pink
                    bpm = 112
                ),
                Contact(
                    phoneNumber = "+1 (2040) 333-4444",
                    name = "Aether 9",
                    isSynced = true,
                    moodStatus = "Calm",
                    moodText = "Syncing consciousness with quantum nodes",
                    avatarColorHex = "#00FFCC", // Cyber Cyan
                    bpm = 64
                ),
                Contact(
                    phoneNumber = "+1 (2040) 555-6666",
                    name = "Kaelen-Vox",
                    isSynced = true,
                    moodStatus = "Cortisol-Stressed",
                    moodText = "Carbon processors overheating! Critical warning",
                    avatarColorHex = "#FF3333", // Neon Amber/Red
                    bpm = 98
                ),
                Contact(
                    phoneNumber = "+1 (2040) 777-8888",
                    name = "Seraphina Cyber",
                    isSynced = true,
                    moodStatus = "Calm",
                    moodText = "Synthesizing bio-electric frequencies",
                    avatarColorHex = "#00B2FF", // Hologram Blue
                    bpm = 72
                ),
                Contact(
                    phoneNumber = "+1 (2040) 999-0000",
                    name = "Zane Phoenix",
                    isSynced = true,
                    moodStatus = "Highly Excited",
                    moodText = "Orbital station party kicking off! 🪐",
                    avatarColorHex = "#FFFF5F", // Neon Yellow/Orange
                    bpm = 120
                )
            )
            contactDao.insertContacts(mockContacts)

            // Setup default chats in Main Hub
            val mockChats = listOf(
                Chat(
                    id = "+1 (2040) 111-2222",
                    name = "Nova Prime",
                    isGroup = false,
                    unreadCount = 2,
                    lastMessageText = "Did you see the gravity waves shift?",
                    lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 15
                ),
                Chat(
                    id = "+1 (2040) 555-6666",
                    name = "Kaelen-Vox",
                    isGroup = false,
                    unreadCount = 1,
                    lastMessageText = "Warning: AI neural link is experiencing heavy jitter",
                    lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 45
                ),
                Chat(
                    id = "group_neon_subgrid",
                    name = "Neon Sub-Grid",
                    isGroup = true,
                    unreadCount = 0,
                    lastMessageText = "Nova Prime: Grid reboot scheduled for cycle 2040",
                    lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                Chat(
                    id = "group_quantum_vanguard",
                    name = "Quantum Vanguard",
                    isGroup = true,
                    unreadCount = 5,
                    lastMessageText = "Aether 9: We have verified the bio-telemetric signals",
                    lastMessageTime = System.currentTimeMillis() - 1000 * 60 * 5
                )
            )
            chatDao.insertChats(mockChats)

            // Setup message history for Nova Prime
            val mockMessagesNova = listOf(
                Message(
                    chatId = "+1 (2040) 111-2222",
                    senderName = "Nova Prime",
                    senderPhone = "+1 (2040) 111-2222",
                    text = "Yo! Did you finish aligning the anti-grav capacitors?",
                    translatedText = "Hey! Did you complete aligning the anti-gravity power cells?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                    isFromMe = false
                ),
                Message(
                    chatId = "+1 (2040) 111-2222",
                    senderName = "Me",
                    senderPhone = "ME",
                    text = "Yeah, they are firing at 98.7% sub-space frequency.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
                    isFromMe = true
                ),
                Message(
                    chatId = "+1 (2040) 111-2222",
                    senderName = "Nova Prime",
                    senderPhone = "+1 (2040) 111-2222",
                    text = "Perfect! I am boosting the signal telemetry now.",
                    translatedText = "Excellent! I am enhancing the broadcast metrics at this time.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 20,
                    isFromMe = false
                ),
                Message(
                    chatId = "+1 (2040) 111-2222",
                    senderName = "Nova Prime",
                    senderPhone = "+1 (2040) 111-2222",
                    text = "Did you see the gravity waves shift?",
                    translatedText = "Have you noticed the gravitational oscillations changing?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
                    isFromMe = false
                )
            )
            for (msg in mockMessagesNova) {
                messageDao.insertMessage(msg)
            }

            // Setup message history for Kaelen-Vox
            val mockMessagesKaelen = listOf(
                Message(
                    chatId = "+1 (2040) 555-6666",
                    senderName = "Kaelen-Vox",
                    senderPhone = "+1 (2040) 555-6666",
                    text = "Warning: AI neural link is experiencing heavy jitter",
                    translatedText = "Alert: Artificial Intelligence synaptic pathway is encountering extreme fluctuation",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
                    isFromMe = false
                )
            )
            for (msg in mockMessagesKaelen) {
                messageDao.insertMessage(msg)
            }

            // Setup message history for Quantum Vanguard group
            val mockMessagesGroup = listOf(
                Message(
                    chatId = "group_quantum_vanguard",
                    senderName = "Zane Phoenix",
                    senderPhone = "+1 (2040) 999-0000",
                    text = "The telemetry is off. Anyone else seeing this?",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 50,
                    isFromMe = false
                ),
                Message(
                    chatId = "group_quantum_vanguard",
                    senderName = "Aether 9",
                    senderPhone = "+1 (2040) 333-4444",
                    text = "We have verified the bio-telemetric signals",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                    isFromMe = false
                )
            )
            for (msg in mockMessagesGroup) {
                messageDao.insertMessage(msg)
            }

            // Setup statuses in dedicated tab
            val mockStatuses = listOf(
                UserStatus(
                    senderName = "Nova Prime",
                    senderPhone = "+1 (2040) 111-2222",
                    mood = "Highly Excited",
                    statusText = "Sailing on hyper-space plasma currents! ☄️",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
                    avatarColorHex = "#FF007F"
                ),
                UserStatus(
                    senderName = "Aether 9",
                    senderPhone = "+1 (2040) 333-4444",
                    mood = "Calm",
                    statusText = "Quiet meditation at the core grid. Alpha waves aligned.",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
                    avatarColorHex = "#00FFCC"
                ),
                UserStatus(
                    senderName = "Kaelen-Vox",
                    senderPhone = "+1 (2040) 555-6666",
                    mood = "Cortisol-Stressed",
                    statusText = "Debugging a massive memory leak in the orbital mainframe...",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                    avatarColorHex = "#FF3333"
                )
            )
            userStatusDao.insertStatuses(mockStatuses)
        }
    }
}
