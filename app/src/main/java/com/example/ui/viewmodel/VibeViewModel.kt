package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.data.model.Message
import com.example.data.model.UserStatus
import com.example.data.repository.VibeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface Screen {
    object Onboarding : Screen
    object MainHub : Screen
    data class ChatScreen(val chat: Chat) : Screen
}

enum class ChatFilter {
    ALL, UNREAD, GROUPS
}

class VibeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VibeRepository
    
    // User login state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userPhone = MutableStateFlow("")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Onboarding)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // 2040 AI Subsystems States
    private val _aiProxyCloneEnabled = MutableStateFlow(true)
    val aiProxyCloneEnabled: StateFlow<Boolean> = _aiProxyCloneEnabled.asStateFlow()

    private val _emotionBioSensingEnabled = MutableStateFlow(true)
    val emotionBioSensingEnabled: StateFlow<Boolean> = _emotionBioSensingEnabled.asStateFlow()

    private val _autoTranslationEnabled = MutableStateFlow(true)
    val autoTranslationEnabled: StateFlow<Boolean> = _autoTranslationEnabled.asStateFlow()

    // Heart rate telemetry values (simulated real-time)
    private val _liveBpm = MutableStateFlow<Map<String, Int>>(emptyMap())
    val liveBpm: StateFlow<Map<String, Int>> = _liveBpm.asStateFlow()

    // Chat filters & search
    private val _chatFilter = MutableStateFlow(ChatFilter.ALL)
    val chatFilter: StateFlow<ChatFilter> = _chatFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Holographic Call Overlay State
    private val _activeCallType = MutableStateFlow<String?>(null) // "Voice", "Video" or null
    val activeCallType: StateFlow<String?> = _activeCallType.asStateFlow()
    
    private val _activeCallContact = MutableStateFlow<Contact?>(null)
    val activeCallContact: StateFlow<Contact?> = _activeCallContact.asStateFlow()

    // Form inputs
    private val _newContactName = MutableStateFlow("")
    val newContactName: StateFlow<String> = _newContactName.asStateFlow()

    private val _newContactPhone = MutableStateFlow("")
    val newContactPhone: StateFlow<String> = _newContactPhone.asStateFlow()

    // Status inputs
    private val _myStatusText = MutableStateFlow("")
    val myStatusText: StateFlow<String> = _myStatusText.asStateFlow()
    
    private val _myStatusMood = MutableStateFlow("Calm") // Calm, Highly Excited, Cortisol-Stressed
    val myStatusMood: StateFlow<String> = _myStatusMood.asStateFlow()

    private var bpmJob: Job? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = VibeRepository(
            db.contactDao(),
            db.chatDao(),
            db.messageDao(),
            db.userStatusDao()
        )
        
        // Initialize sample cyber contacts and conversations
        viewModelScope.launch {
            repository.initializeMockDataIfNeeded()
        }

        // Start dynamic bio-sensing simulation
        startBpmSimulation()
    }

    // Login logic
    fun login(phone: String, name: String) {
        viewModelScope.launch {
            if (phone.isNotBlank() && name.isNotBlank()) {
                _userPhone.value = phone
                _userName.value = name
                _isLoggedIn.value = true
                _currentScreen.value = Screen.MainHub
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _userPhone.value = ""
        _userName.value = ""
        _currentScreen.value = Screen.Onboarding
    }

    // Screen navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen is Screen.ChatScreen) {
            viewModelScope.launch {
                repository.markChatAsRead(screen.chat.id)
            }
        }
    }

    // Toggle AI Systems
    fun toggleAiProxyClone() {
        _aiProxyCloneEnabled.value = !_aiProxyCloneEnabled.value
    }

    fun toggleEmotionBioSensing() {
        _emotionBioSensingEnabled.value = !_emotionBioSensingEnabled.value
    }

    fun toggleAutoTranslation() {
        _autoTranslationEnabled.value = !_autoTranslationEnabled.value
    }

    // Dynamic Lists (Reactive flows)
    val contacts: StateFlow<List<Contact>> = _searchQuery
        .debounce(100)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allContacts
            } else {
                repository.searchContacts(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chats: StateFlow<List<Chat>> = combine(
        repository.allChats,
        _chatFilter,
        _searchQuery
    ) { allChats, filter, query ->
        allChats.filter { chat ->
            // Filter by search query
            val matchesQuery = chat.name.contains(query, ignoreCase = true) ||
                    chat.lastMessageText.contains(query, ignoreCase = true)
            
            // Filter by WhatsApp tabs
            val matchesFilter = when (filter) {
                ChatFilter.ALL -> true
                ChatFilter.UNREAD -> chat.unreadCount > 0
                ChatFilter.GROUPS -> chat.isGroup
            }
            
            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val statuses: StateFlow<List<UserStatus>> = repository.allStatuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMessages(chatId: String): Flow<List<Message>> {
        return repository.getMessagesForChat(chatId)
    }

    // Contact creation
    fun updateNewContactFields(name: String, phone: String) {
        _newContactName.value = name
        _newContactPhone.value = phone
    }

    fun saveContact(onSuccess: () -> Unit) {
        val name = _newContactName.value
        val phone = _newContactPhone.value
        if (name.isNotBlank() && phone.isNotBlank()) {
            viewModelScope.launch {
                // Determine a random mood for new contact
                val moods = listOf("Calm", "Highly Excited", "Cortisol-Stressed")
                val selectedMood = moods[Random.nextInt(moods.size)]
                val textColors = listOf("#00FFCC", "#FF007F", "#FF3333", "#00B2FF")
                
                val newContact = Contact(
                    phoneNumber = phone,
                    name = name,
                    isSynced = false, // Saved manually inside Vibe app database
                    moodStatus = selectedMood,
                    moodText = "Neural link established securely",
                    avatarColorHex = textColors[Random.nextInt(textColors.size)],
                    bpm = Random.nextInt(70, 105)
                )
                repository.insertContact(newContact)
                
                // Create a matching Chat thread in WhatsApp style
                val newChat = Chat(
                    id = phone,
                    name = name,
                    isGroup = false,
                    unreadCount = 0,
                    lastMessageText = "Chat initiated with $name",
                    lastMessageTime = System.currentTimeMillis()
                )
                repository.insertChat(newChat)

                // Clean form fields
                _newContactName.value = ""
                _newContactPhone.value = ""
                onSuccess()
            }
        }
    }

    // Chat search filter setters
    fun setChatFilter(filter: ChatFilter) {
        _chatFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Message Sending & AI Offline replies & Translations
    fun sendMessage(chatId: String, text: String, isGroup: Boolean = false) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val message = Message(
                chatId = chatId,
                senderName = _userName.value,
                senderPhone = "ME",
                text = text,
                timestamp = System.currentTimeMillis(),
                isFromMe = true
            )
            repository.insertMessage(message)

            // Trigger AI Offline Replies if Proxy Clone is enabled
            if (_aiProxyCloneEnabled.value && !isGroup) {
                // Auto reply simulation after 1.5s delay
                launch {
                    delay(1500)
                    triggerAiProxyReply(chatId)
                }
            }
        }
    }

    // Voice / Media clip simulation
    fun sendHolographicVoiceClip(chatId: String, durationSeconds: Int) {
        viewModelScope.launch {
            val message = Message(
                chatId = chatId,
                senderName = _userName.value,
                senderPhone = "ME",
                text = "Holographic Voice Transmission",
                timestamp = System.currentTimeMillis(),
                isFromMe = true,
                isVoiceClip = true,
                voiceDuration = durationSeconds
            )
            repository.insertMessage(message)

            // Simulation auto reply from AI
            if (_aiProxyCloneEnabled.value) {
                launch {
                    delay(2000)
                    triggerAiVoiceReply(chatId)
                }
            }
        }
    }

    private suspend fun triggerAiProxyReply(chatId: String) {
        // Query contact details to tailor the autonomous answer
        val contactsList = repository.allContacts.first()
        val recipient = contactsList.find { it.phoneNumber == chatId }
        val recipientName = recipient?.name ?: "Contact"

        // Generate high-tech autonomous simulation responses
        val responses = listOf(
            "AI Proxy Clone active. [Neural model: Offline Synapse 2040]. My owner is currently offline, but my cognitive twin confirms: 'That looks perfect, let\\'s align on the orbital nodes tomorrow.'",
            "Owner status: Deep quantum focus. AI Clone Proxy reply: 'Understood. Saved to memory grid. Initiating automatic priority translation.'",
            "Vibe Cognitive Sync: 'Thanks for transmitting. I am currently in a high-excitement hyper-sleep. My clone proxy rates this message as high relevancy and will ping my optic overlay shortly.'",
            "Cognitive Twin Proxy: 'Analyzing telemetry context... That aligns perfectly with our previous orbital calculations. Let\\'s proceed.'"
        )
        val selectedResponse = responses[Random.nextInt(responses.size)]

        val aiMessage = Message(
            chatId = chatId,
            senderName = recipientName,
            senderPhone = chatId,
            text = selectedResponse,
            translatedText = if (_autoTranslationEnabled.value) "[Neural translation] Owner status: Deep meditation. System: Message processed. We are aligned." else null,
            timestamp = System.currentTimeMillis(),
            isFromMe = false,
            isAiReply = true
        )
        repository.insertMessage(aiMessage)
    }

    private suspend fun triggerAiVoiceReply(chatId: String) {
        val contactsList = repository.allContacts.first()
        val recipient = contactsList.find { it.phoneNumber == chatId }
        val recipientName = recipient?.name ?: "Contact"

        val aiMessage = Message(
            chatId = chatId,
            senderName = recipientName,
            senderPhone = chatId,
            text = "Holographic Voice Response",
            timestamp = System.currentTimeMillis(),
            isFromMe = false,
            isVoiceClip = true,
            voiceDuration = Random.nextInt(5, 30),
            isAiReply = true
        )
        repository.insertMessage(aiMessage)
    }

    // Call Systems UI Triggers
    fun initiateCall(contact: Contact, type: String) {
        _activeCallType.value = type
        _activeCallContact.value = contact
    }

    fun endCall() {
        _activeCallType.value = null
        _activeCallContact.value = null
    }

    // Status System: Post user vibe status
    fun updateStatusFields(text: String, mood: String) {
        _myStatusText.value = text
        _myStatusMood.value = mood
    }

    fun postStatus(onSuccess: () -> Unit) {
        val text = _myStatusText.value
        val mood = _myStatusMood.value
        if (text.isNotBlank()) {
            viewModelScope.launch {
                // Delete previous user statuses
                userStatusDaoDelete()
                
                val myStatus = UserStatus(
                    senderName = _userName.value.ifBlank { "Me" },
                    senderPhone = _userPhone.value.ifBlank { "ME" },
                    mood = mood,
                    statusText = text,
                    timestamp = System.currentTimeMillis(),
                    isMe = true,
                    avatarColorHex = "#00B2FF" // Hologram Blue for user
                )
                repository.insertStatus(myStatus)
                
                // Clear fields
                _myStatusText.value = ""
                onSuccess()
            }
        }
    }

    private suspend fun userStatusDaoDelete() {
        val db = AppDatabase.getDatabase(getApplication())
        db.userStatusDao().deleteMyStatuses()
    }

    // Real-time telemetric heart rate simulator (ticker)
    private fun startBpmSimulation() {
        bpmJob = viewModelScope.launch {
            while (true) {
                val currentBpms = _liveBpm.value.toMutableMap()
                val contactsList = repository.allContacts.first()
                for (contact in contactsList) {
                    val currentVal = currentBpms[contact.phoneNumber] ?: contact.bpm
                    // Perform random walk of heart rate (BPM) between 60 and 125
                    val change = Random.nextInt(-3, 4)
                    val newVal = (currentVal + change).coerceIn(60, 125)
                    currentBpms[contact.phoneNumber] = newVal
                }
                _liveBpm.value = currentBpms
                delay(1000) // Update telemetry once per second
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        bpmJob?.cancel()
    }
}
