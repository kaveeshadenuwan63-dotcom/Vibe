package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Chat
import com.example.data.model.Contact
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatFilter
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.VibeViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHubScreen(
    viewModel: VibeViewModel,
    onNavigateToChat: (chat: Chat) -> Unit
) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val statuses by viewModel.statuses.collectAsStateWithLifecycle()
    val currentFilter by viewModel.chatFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // 2040 AI Subsystems
    val aiProxyEnabled by viewModel.aiProxyCloneEnabled.collectAsStateWithLifecycle()
    val bioSensingEnabled by viewModel.emotionBioSensingEnabled.collectAsStateWithLifecycle()
    val translationEnabled by viewModel.autoTranslationEnabled.collectAsStateWithLifecycle()
    val liveBpm by viewModel.liveBpm.collectAsStateWithLifecycle()

    // Form states for saving contact
    val newName by viewModel.newContactName.collectAsStateWithLifecycle()
    val newPhone by viewModel.newContactPhone.collectAsStateWithLifecycle()

    // Local UI screens/tabs: 0 = Chats, 1 = Updates (Statuses), 2 = Contacts
    var selectedTab by remember { mutableStateOf(0) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var showAiControlPanel by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    CyberSpaceBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                // Futuristic Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x1F07090E))
                        .border(1.dp, GlassWhiteBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HolographicTitle("VIBE", fontSize = 24)

                        // 2040 AI Status pills shown on Header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showAiControlPanel = !showAiControlPanel }) {
                                Icon(
                                    imageVector = Icons.Filled.SettingsInputAntenna,
                                    contentDescription = "AI Control Center",
                                    tint = if (aiProxyEnabled || bioSensingEnabled || translationEnabled) CyberCyan else GlassTextSecondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { viewModel.logout() }) {
                                Icon(
                                    imageVector = Icons.Filled.Logout,
                                    contentDescription = "De-Authorize Synapse",
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Search bar (Frosted Glass style)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search synced & saved contacts...", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = GlassTextSecondary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            cursorColor = CyberCyan,
                            focusedContainerColor = Color(0x0CFFFFFF),
                            unfocusedContainerColor = Color(0x0CFFFFFF)
                        ),
                        textStyle = TextStyle(color = GlassTextPrimary, fontSize = 14.sp),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("search_bar"),
                        singleLine = true
                    )

                    // Control shelf expanded drawer
                    AnimatedVisibility(
                        visible = showAiControlPanel,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xD90F121D))
                                .border(1.dp, GlassCyanBorder)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "2040 NEURAL CONTROL INTERFACE",
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Toggle 1: AI Proxy Clone
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("AI Proxy Clone [Offline Replies]", color = GlassTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Autonomous twin responds when offline", color = GlassTextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = aiProxyEnabled,
                                    onCheckedChange = { viewModel.toggleAiProxyClone() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = CyberCyan,
                                        checkedTrackColor = CyberCyan.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("ai_proxy_toggle")
                                )
                            }

                            Divider(color = GlassWhiteBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                            // Toggle 2: Emotion Bio-Sensing Telemetry
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Emotion Bio-Sensing Overlay", color = GlassTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Live biometric heart rate (BPM) logs", color = GlassTextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = bioSensingEnabled,
                                    onCheckedChange = { viewModel.toggleEmotionBioSensing() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = HologramBlue,
                                        checkedTrackColor = HologramBlue.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("bio_sensing_toggle")
                                )
                            }

                            Divider(color = GlassWhiteBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 6.dp))

                            // Toggle 3: Auto-Translation Neural Link
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Auto-Translation Neural Link", color = GlassTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Dynamic conversion of cosmic languages", color = GlassTextSecondary, fontSize = 10.sp)
                                }
                                Switch(
                                    checked = translationEnabled,
                                    onCheckedChange = { viewModel.toggleAutoTranslation() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = NeonPink,
                                        checkedTrackColor = NeonPink.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("translation_toggle")
                                )
                            }
                        }
                    }

                    // Filters Row (All, Unread, Groups) - ONLY when Chats tab is selected
                    if (selectedTab == 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            ChatFilterChip(
                                text = "All",
                                active = currentFilter == ChatFilter.ALL,
                                onClick = { viewModel.setChatFilter(ChatFilter.ALL) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ChatFilterChip(
                                text = "Unread",
                                active = currentFilter == ChatFilter.UNREAD,
                                onClick = { viewModel.setChatFilter(ChatFilter.UNREAD) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ChatFilterChip(
                                text = "Groups",
                                active = currentFilter == ChatFilter.GROUPS,
                                onClick = { viewModel.setChatFilter(ChatFilter.GROUPS) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            bottomBar = {
                // WhatsApp Style Bottom Navigation Bar
                NavigationBar(
                    containerColor = Color(0x2B07090E),
                    modifier = Modifier
                        .border(1.dp, GlassWhiteBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Chats", color = if (selectedTab == 0) CyberCyan else GlassTextSecondary, fontSize = 12.sp) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.ChatBubble else Icons.Filled.ChatBubbleOutline,
                                contentDescription = "Chats",
                                tint = if (selectedTab == 0) CyberCyan else GlassTextSecondary
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Updates", color = if (selectedTab == 1) CyberCyan else GlassTextSecondary, fontSize = 12.sp) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.FiberSmartRecord else Icons.Filled.FilterTiltShift,
                                contentDescription = "Updates",
                                tint = if (selectedTab == 1) CyberCyan else GlassTextSecondary
                            )
                        }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Contacts", color = if (selectedTab == 2) CyberCyan else GlassTextSecondary, fontSize = 12.sp) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.People else Icons.Filled.PeopleOutline,
                                contentDescription = "Contacts",
                                tint = if (selectedTab == 2) CyberCyan else GlassTextSecondary
                            )
                        }
                    )
                }
            },
            floatingActionButton = {
                // Floating Action Button to 'Save New Contact' manually
                FloatingActionButton(
                    onClick = { showAddContactDialog = true },
                    containerColor = CyberCyan,
                    contentColor = Color.Black,
                    modifier = Modifier.testTag("add_contact_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.PersonAdd,
                        contentDescription = "Save New Contact"
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Render view based on WhatsApp tab
                when (selectedTab) {
                    0 -> ChatsTabContent(
                        chats = chats,
                        liveBpm = liveBpm,
                        bioSensingEnabled = bioSensingEnabled,
                        onNavigateToChat = onNavigateToChat
                    )
                    1 -> UpdatesTabContent(
                        viewModel = viewModel,
                        statuses = statuses
                    )
                    2 -> ContactsTabContent(
                        contacts = contacts,
                        liveBpm = liveBpm,
                        bioSensingEnabled = bioSensingEnabled,
                        onStartChat = { contact ->
                            // Open or create individual chat for this contact
                            val chat = Chat(
                                id = contact.phoneNumber,
                                name = contact.name,
                                isGroup = false,
                                lastMessageText = contact.moodText
                            )
                            viewModel.navigateTo(Screen.ChatScreen(chat))
                        }
                    )
                }
            }
        }

        // Save New Contact Modal Dialog
        if (showAddContactDialog) {
            AlertDialog(
                onDismissRequest = { showAddContactDialog = false },
                containerColor = ObsidianSurface,
                modifier = Modifier.border(1.dp, GlassCyanBorder, RoundedCornerShape(28.dp)),
                title = {
                    Text(
                        "ADD TELEPATHIC SYNAPSE",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        style = TextStyle(shadow = Shadow(color = CyberCyan, blurRadius = 8f))
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Manually input contact credentials. Saved contacts sync instantly inside the local secure database.",
                            color = GlassTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Name input
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { viewModel.updateNewContactFields(it, newPhone) },
                            label = { Text("Quantum Signature (Name)", color = GlassTextSecondary) },
                            textStyle = TextStyle(color = GlassTextPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = GlassWhiteBorder,
                                cursorColor = CyberCyan
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("modal_contact_name")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Phone input
                        OutlinedTextField(
                            value = newPhone,
                            onValueChange = { viewModel.updateNewContactFields(newName, it) },
                            label = { Text("Neural Comm Code (Phone)", color = GlassTextSecondary) },
                            textStyle = TextStyle(color = GlassTextPrimary),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = GlassWhiteBorder,
                                cursorColor = CyberCyan
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("modal_contact_phone"),
                            placeholder = { Text("+1 (2040) ...", color = Color.Gray) }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.saveContact {
                                showAddContactDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                        modifier = Modifier.testTag("save_contact_button")
                    ) {
                        Text("SAVE SYNAPSE")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showAddContactDialog = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = GlassTextSecondary)
                    ) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

// Filter chip styled to resemble M3 and Frosted Glass
@Composable
fun ChatFilterChip(
    text: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) CyberCyan.copy(alpha = 0.25f) else Color(0x0CFFFFFF))
            .border(1.dp, if (active) CyberCyan else GlassWhiteBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (active) CyberCyan else GlassTextSecondary,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            style = TextStyle(
                shadow = if (active) Shadow(color = CyberCyan, blurRadius = 4f) else null
            )
        )
    }
}

// TABS RENDERING:
// 1. Chats Tab
@Composable
fun ChatsTabContent(
    chats: List<Chat>,
    liveBpm: Map<String, Int>,
    bioSensingEnabled: Boolean,
    onNavigateToChat: (chat: Chat) -> Unit
) {
    if (chats.isEmpty()) {
        EmptyStatePlaceholder(
            icon = Icons.Filled.ChatBubbleOutline,
            message = "No dynamic synapses initialized yet. Sync phonebook or add custom contacts!"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            items(chats) { chat ->
                ChatItemRow(
                    chat = chat,
                    bpmValue = liveBpm[chat.id],
                    bioSensingEnabled = bioSensingEnabled,
                    onClick = { onNavigateToChat(chat) }
                )
            }
        }
    }
}

// 2. Contacts Tab
@Composable
fun ContactsTabContent(
    contacts: List<Contact>,
    liveBpm: Map<String, Int>,
    bioSensingEnabled: Boolean,
    onStartChat: (contact: Contact) -> Unit
) {
    if (contacts.isEmpty()) {
        EmptyStatePlaceholder(
            icon = Icons.Filled.PeopleOutline,
            message = "No telepathic links established. Save a new contact credentials."
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "COMMUNICATION DIRECTORY",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(contacts) { contact ->
                ContactItemRow(
                    contact = contact,
                    bpmValue = liveBpm[contact.phoneNumber],
                    bioSensingEnabled = bioSensingEnabled,
                    onClick = { onStartChat(contact) }
                )
            }
        }
    }
}

// Chat list row rendering with custom design
@Composable
fun ChatItemRow(
    chat: Chat,
    bpmValue: Int?,
    bioSensingEnabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with Glowing Mood Ring based on chat type
        val initialLetter = if (chat.name.isNotEmpty()) chat.name.take(2).uppercase() else "C"
        val mood = if (chat.isGroup) "Me" else when (chat.id) {
            "+1 (2040) 111-2222" -> "Highly Excited"
            "+1 (2040) 555-6666" -> "Cortisol-Stressed"
            else -> "Calm"
        }
        
        MoodRingAvatar(
            mood = mood,
            initials = initialLetter,
            avatarColor = if (chat.isGroup) HologramBlue else CyberCyan,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name & messages
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.name,
                        color = GlassTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Emotion Bio-Sensing heart rate pulsing
                    if (bioSensingEnabled && !chat.isGroup) {
                        val finalBpm = bpmValue ?: 72
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Heart pulse animation
                        val infiniteTransition = rememberInfiniteTransition(label = "heart")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.9f,
                            targetValue = 1.15f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scale"
                        )
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Live Heart Rate",
                                tint = if (finalBpm > 95) NeonPink else NeonGreen,
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.Transparent)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$finalBpm BPM",
                                color = if (finalBpm > 95) NeonPink else NeonGreen,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Format timestamp
                Text(
                    text = formatTimestamp(chat.lastMessageTime),
                    color = GlassTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chat.lastMessageText,
                    color = GlassTextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (chat.unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(CyberCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Contact Row component
@Composable
fun ContactItemRow(
    contact: Contact,
    bpmValue: Int?,
    bioSensingEnabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoodRingAvatar(
            mood = contact.moodStatus,
            initials = contact.name.take(2).uppercase(),
            avatarColor = Color(android.graphics.Color.parseColor(contact.avatarColorHex)),
            size = 46.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = contact.name,
                    color = GlassTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                if (bioSensingEnabled) {
                    val finalBpm = bpmValue ?: contact.bpm
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "❤ $finalBpm BPM",
                        color = if (finalBpm > 95) NeonPink else CyberCyan,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = contact.phoneNumber,
                color = GlassTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Mood status: ${contact.moodStatus} • \"${contact.moodText}\"",
                color = GlassTextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = "Message",
                tint = CyberCyan
            )
        }
    }
}

// Empty state indicator to align with design specs
@Composable
fun EmptyStatePlaceholder(
    icon: ImageVector,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GlassWhiteBorder,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = GlassTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

// Simple helper to format dates beautifully
fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
