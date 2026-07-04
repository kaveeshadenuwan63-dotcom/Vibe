package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.ui.components.CyberSpaceBackground
import com.example.ui.components.FrostedGlassButton
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.MoodRingAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.VibeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: VibeViewModel,
    chat: Chat,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.getMessages(chat.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val liveBpm by viewModel.liveBpm.collectAsStateWithLifecycle()
    val bioSensingEnabled by viewModel.emotionBioSensingEnabled.collectAsStateWithLifecycle()
    val translationEnabled by viewModel.autoTranslationEnabled.collectAsStateWithLifecycle()

    // Calling System overlay triggers
    val activeCallType by viewModel.activeCallType.collectAsStateWithLifecycle()
    val activeCallContact by viewModel.activeCallContact.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var showMediaPopup by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Scroll to bottom when messages list size increases
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    CyberSpaceBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                // Frosted Glass Chat Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x240F121D))
                        .border(1.dp, GlassWhiteBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CyberCyan)
                    }

                    // User avatar with dynamic mood ring
                    val mood = if (chat.isGroup) "Me" else when (chat.id) {
                        "+1 (2040) 111-2222" -> "Highly Excited"
                        "+1 (2040) 555-6666" -> "Cortisol-Stressed"
                        else -> "Calm"
                    }
                    MoodRingAvatar(
                        mood = mood,
                        initials = chat.name.take(2).uppercase(),
                        avatarColor = if (chat.isGroup) HologramBlue else CyberCyan,
                        size = 40.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Name and Live Bio-sensing Telemetry Sub-panel
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = chat.name,
                            color = GlassTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        
                        // Heart rate live overlay banner
                        if (bioSensingEnabled && !chat.isGroup) {
                            val bpm = liveBpm[chat.id] ?: 72
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = "Simulated Biometrics",
                                    tint = if (bpm > 95) NeonPink else NeonGreen,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$bpm BPM • ${if (bpm > 95) "Elevated Stress" else "Calm Waves"}",
                                    color = if (bpm > 95) NeonPink else NeonGreen,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = if (chat.isGroup) "3 synaptic links active" else "Neural link established",
                                color = GlassTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Voice and Video Calling top-right action buttons
                    if (!chat.isGroup) {
                        IconButton(onClick = {
                            // Trigger Voice Call Fullscreen Overlay
                            val contact = com.example.data.model.Contact(
                                phoneNumber = chat.id,
                                name = chat.name,
                                isSynced = true,
                                moodStatus = mood,
                                moodText = "Direct comm link"
                            )
                            viewModel.initiateCall(contact, "Voice")
                        }) {
                            Icon(Icons.Outlined.Mic, contentDescription = "Sub-space Voice Link", tint = CyberCyan)
                        }
                        IconButton(onClick = {
                            // Trigger Video Call Fullscreen Overlay
                            val contact = com.example.data.model.Contact(
                                phoneNumber = chat.id,
                                name = chat.name,
                                isSynced = true,
                                moodStatus = mood,
                                moodText = "Hologram overlay active"
                            )
                            viewModel.initiateCall(contact, "Video")
                        }) {
                            Icon(Icons.Outlined.Videocam, contentDescription = "Hologram Video Link", tint = HologramBlue)
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Messages List Scroll
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 80.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        MessageBubble(
                            message = msg,
                            translationEnabled = translationEnabled
                        )
                    }
                }

                // Bottom typing input row with Holographic Media Button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xFF07090E))
                            )
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x1F0F121D), RoundedCornerShape(24.dp))
                            .border(1.dp, GlassWhiteBorder, RoundedCornerShape(24.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Holographic Media Selector Button (for next-gen voice/video clips)
                        IconButton(
                            onClick = { showMediaPopup = true },
                            modifier = Modifier.testTag("holo_media_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterTiltShift,
                                contentDescription = "Holographic Media",
                                tint = CyberCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        // Message text box input
                        TextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = { Text("Transmit text wave...", color = Color.Gray, fontSize = 14.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedTextColor = GlassTextPrimary,
                                unfocusedTextColor = GlassTextPrimary,
                                cursorColor = CyberCyan,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_input_box"),
                            maxLines = 4
                        )

                        // Send Button
                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    viewModel.sendMessage(chat.id, textInput, chat.isGroup)
                                    textInput = ""
                                }
                            },
                            modifier = Modifier.testTag("send_msg_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Transmit Message",
                                tint = CyberCyan
                            )
                        }
                    }
                }

                // Holographic Media Action Popup overlay selection
                if (showMediaPopup) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x99000000))
                            .clickable { showMediaPopup = false },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        FrostedGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable(enabled = false) {},
                            borderColor = GlassCyanBorder
                        ) {
                            Text(
                                text = "HOLOGRAPHIC SYNAPSE TRANSMISSION",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                // Option 1: Simulated voice clip
                                MediaOptionButton(
                                    icon = Icons.Filled.Mic,
                                    label = "Holo Voice Wave",
                                    color = CyberCyan,
                                    onClick = {
                                        viewModel.sendHolographicVoiceClip(chat.id, Random.nextInt(10, 45))
                                        showMediaPopup = false
                                    }
                                )

                                // Option 2: Simulated hologram message
                                MediaOptionButton(
                                    icon = Icons.Filled.Videocam,
                                    label = "Video Hologram",
                                    color = HologramBlue,
                                    onClick = {
                                        // Send a holographic video text entry
                                        viewModel.sendMessage(chat.id, "📹 Simulated Holographic Video Synapse recorded [Duration: 12s, 4K holographic grid].", chat.isGroup)
                                        showMediaPopup = false
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            FrostedGlassButton(
                                onClick = { showMediaPopup = false },
                                text = "ABORT TRANSMISSION",
                                glowColor = GlassTextSecondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Fullscreen calling hologram simulation overlay! (Highly immersive, fulfillsVoice/Video Calling spec)
        if (activeCallType != null && activeCallContact != null) {
            FullscreenCallingOverlay(
                callType = activeCallType!!,
                contact = activeCallContact!!,
                onEndCall = { viewModel.endCall() }
            )
        }
    }
}

// Media Option selector layout
@Composable
fun MediaOptionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(1.5.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = GlassTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Chat message speech bubble with glowing border and Neural Translation link details
@Composable
fun MessageBubble(
    message: Message,
    translationEnabled: Boolean
) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isFromMe) Color(0x3300FFCC) else Color(0x1BFFFFFF)
    val borderColor = if (message.isFromMe) CyberCyan.copy(alpha = 0.4f) else GlassWhiteBorder
    val textColor = if (message.isFromMe) CyberCyan else GlassTextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Sender name for groups or AI Clone replies
            if (!message.isFromMe) {
                Text(
                    text = if (message.isAiReply) "${message.senderName} [AI Proxy]" else message.senderName,
                    color = if (message.isAiReply) CyberCyan else HologramBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }

            // Actual bubble container
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromMe) 16.dp else 2.dp,
                            bottomEnd = if (message.isFromMe) 2.dp else 16.dp
                        )
                    )
                    .background(bubbleColor)
                    .border(
                        1.dp,
                        borderColor,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (message.isFromMe) 16.dp else 2.dp,
                            bottomEnd = if (message.isFromMe) 2.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column {
                    if (message.isVoiceClip) {
                        // Futuristic visual audio voice wave
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Voice clip",
                                tint = textColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Wave elements representation
                            Text(
                                text = "|||||il|i|ll||i|l||i|| ",
                                color = textColor,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${message.voiceDuration}s",
                                color = GlassTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        // Standard text
                        Text(
                            text = message.text,
                            color = GlassTextPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Neural Link Auto-translation Overlay sub-hud (glow text)
            if (translationEnabled && message.translatedText != null && !message.isFromMe) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp, start = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0x1F00FFCC))
                        .border(0.5.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Translate,
                            contentDescription = "Neural translation link",
                            tint = CyberCyan,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = message.translatedText ?: "",
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Timestamp representation
            Text(
                text = formatTimestamp(message.timestamp),
                color = GlassTextSecondary,
                fontSize = 9.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 0.dp)
            )
        }
    }
}

// Fullscreen calling mockup (Immersive 2040 Holographic Display Interface)
@Composable
fun FullscreenCallingOverlay(
    callType: String, // "Voice" or "Video"
    contact: com.example.data.model.Contact,
    onEndCall: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "calling_particles")
    val gridAnimationOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF907090E)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Background grid scanning animation lines representing 2040 cyber scanner
            val strokeVal = 0.5.dp.toPx()
            val spacing = 50f
            var x = gridAnimationOffset % spacing
            while (x < size.width) {
                drawLine(
                    color = CyberCyan.copy(alpha = 0.08f),
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x, size.height),
                    strokeWidth = strokeVal
                )
                x += spacing
            }
            var y = gridAnimationOffset % spacing
            while (y < size.height) {
                drawLine(
                    color = CyberCyan.copy(alpha = 0.08f),
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                    strokeWidth = strokeVal
                )
                y += spacing
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "SUB-SPACE SECURE COMM LINK",
                color = CyberCyan,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                style = TextStyle(shadow = Shadow(color = CyberCyan, blurRadius = 8f)),
                modifier = Modifier.padding(bottom = 36.dp)
            )

            // Animated pulsing avatar
            Box(contentAlignment = Alignment.Center) {
                // Glowing radar waves behind avatar
                val scaleWave by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.6f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "wave"
                )
                val alphaWave by infiniteTransition.animateFloat(
                    initialValue = 0.6f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, CyberCyan.copy(alpha = alphaWave), CircleShape)
                        .background(Color.Transparent)
                )

                MoodRingAvatar(
                    mood = contact.moodStatus,
                    initials = contact.name.take(2).uppercase(),
                    avatarColor = CyberCyan,
                    size = 110.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Calling status
            Text(
                text = contact.name,
                color = GlassTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "ESTABLISHING HOLO-${callType.uppercase()} NEURAL OVERLAY...",
                color = HologramBlue,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Action call buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, GlassWhiteBorder, CircleShape)
                ) {
                    Icon(Icons.Filled.MicOff, contentDescription = "Mute", tint = GlassTextPrimary)
                }

                // End call (Red button)
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(NeonPink.copy(alpha = 0.8f))
                        .border(2.dp, NeonPink, CircleShape)
                        .testTag("end_call_button")
                ) {
                    Icon(Icons.Filled.CallEnd, contentDescription = "Disconnect Neural Link", tint = Color.White)
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, GlassWhiteBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (callType == "Video") Icons.Filled.VideocamOff else Icons.Filled.VolumeUp,
                        contentDescription = "Toggle",
                        tint = GlassTextPrimary
                    )
                }
            }
        }
    }
}
