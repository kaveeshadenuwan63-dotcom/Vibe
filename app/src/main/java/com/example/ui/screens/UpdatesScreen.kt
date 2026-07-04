package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FiberSmartRecord
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserStatus
import com.example.ui.components.FrostedGlassButton
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.MoodRingAvatar
import com.example.ui.theme.*
import com.example.ui.viewmodel.VibeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesTabContent(
    viewModel: VibeViewModel,
    statuses: List<UserStatus>
) {
    val myStatusText by viewModel.myStatusText.collectAsStateWithLifecycle()
    val myStatusMood by viewModel.myStatusMood.collectAsStateWithLifecycle()
    val myName by viewModel.userName.collectAsStateWithLifecycle()

    var showPostStatusDialog by remember { mutableStateOf(false) }

    // Separate my status from other contacts status
    val myStatus = statuses.find { it.isMe }
    val contactStatuses = statuses.filter { !it.isMe }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("updates_tab"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // My Status Section
        item {
            Text(
                text = "MY NEURAL STATUS",
                color = CyberCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            FrostedGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPostStatusDialog = true }
                    .padding(bottom = 24.dp),
                borderColor = GlassWhiteBorder
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        MoodRingAvatar(
                            mood = myStatus?.mood ?: "Me",
                            initials = if (myName.isNotEmpty()) myName.take(2).uppercase() else "ME",
                            avatarColor = HologramBlue,
                            size = 52.dp
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(CyberCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Status",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (myStatus != null) "My Active Vibe" else "Transmit My Vibe Status",
                            color = GlassTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = myStatus?.statusText ?: "Share your live mental & biological frequency...",
                            color = GlassTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Recent Updates Section
        item {
            Text(
                text = "RECENT TELEMETRIC STATUS UPDATES",
                color = CyberCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (contactStatuses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = HologramBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "No synaptic status updates yet in your grid sector.",
                            color = GlassTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(contactStatuses) { status ->
                StatusUpdateRow(status = status)
            }
        }
    }

    // Modal to write and post a custom status with mood selection
    if (showPostStatusDialog) {
        AlertDialog(
            onDismissRequest = { showPostStatusDialog = false },
            containerColor = ObsidianSurface,
            modifier = Modifier.border(1.dp, GlassCyanBorder, RoundedCornerShape(28.dp)),
            title = {
                Text(
                    "TRANSMIT NEURAL VIBE",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(shadow = Shadow(color = CyberCyan, blurRadius = 8f))
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Broadcast your physical state and current thought-stream across the secure network grid.",
                        color = GlassTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Text status input
                    OutlinedTextField(
                        value = myStatusText,
                        onValueChange = { viewModel.updateStatusFields(it, myStatusMood) },
                        label = { Text("What is your current frequency?", color = GlassTextSecondary) },
                        textStyle = TextStyle(color = GlassTextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            cursorColor = CyberCyan
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("status_text_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "SELECT BIOMETRIC MOOD RAMP:",
                        color = CyberCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Mood Ring Selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoodSelectorItem(
                            mood = "Calm",
                            selected = myStatusMood == "Calm",
                            color = MoodCalmColor,
                            onClick = { viewModel.updateStatusFields(myStatusText, "Calm") }
                        )
                        MoodSelectorItem(
                            mood = "Highly Excited",
                            selected = myStatusMood == "Highly Excited",
                            color = MoodExcitedColor,
                            onClick = { viewModel.updateStatusFields(myStatusText, "Highly Excited") }
                        )
                        MoodSelectorItem(
                            mood = "Cortisol-Stressed",
                            selected = myStatusMood == "Cortisol-Stressed",
                            color = MoodStressedColor,
                            onClick = { viewModel.updateStatusFields(myStatusText, "Cortisol-Stressed") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.postStatus {
                            showPostStatusDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                    modifier = Modifier.testTag("post_status_button")
                ) {
                    Text("BROADCAST VIBE")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPostStatusDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = GlassTextSecondary)
                ) {
                    Text("ABORT")
                }
            }
        )
    }
}

// Interactive Mood item selector
@Composable
fun MoodSelectorItem(
    mood: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.15f) else Color(0x0CFFFFFF))
            .border(1.dp, if (selected) color else GlassWhiteBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mood.replace("-", " "),
                color = if (selected) color else GlassTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// Contacts Status Update list row
@Composable
fun StatusUpdateRow(status: UserStatus) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoodRingAvatar(
            mood = status.mood,
            initials = status.senderName.take(2).uppercase(),
            avatarColor = Color(android.graphics.Color.parseColor(status.avatarColorHex)),
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = status.senderName,
                color = GlassTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "\"${status.statusText}\"",
                color = GlassTextPrimary.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Mood Index: ${status.mood} • ${formatTimestamp(status.timestamp)}",
                color = GlassTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
