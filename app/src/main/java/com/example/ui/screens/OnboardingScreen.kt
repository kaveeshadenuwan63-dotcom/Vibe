package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberSpaceBackground
import com.example.ui.components.FrostedGlassButton
import com.example.ui.components.FrostedGlassCard
import com.example.ui.components.HolographicTitle
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onLoginSuccess: (phone: String, username: String) -> Unit
) {
    var phoneInput by remember { mutableStateFlowOf("") }
    var nameInput by remember { mutableStateFlowOf("") }
    var isOtpSent by remember { mutableStateFlowOf(false) }
    var otpInput by remember { mutableStateFlowOf("") }
    var errorMessage by remember { mutableStateFlowOf<String?>(null) }

    CyberSpaceBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Cyber Brand Header
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(CyberCyan.copy(alpha = 0.4f), Color.Transparent)))
                    .border(2.dp, CyberCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Fingerprint,
                    contentDescription = "Holographic Neural Identity Key",
                    tint = CyberCyan,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            HolographicTitle("VIBE 2040", fontSize = 28, align = TextAlign.Center)
            
            Text(
                text = "Quantum Encrypted Synapse Sync",
                color = GlassTextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                style = TextStyle(letterSpacing = 2.sp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Main login card
            FrostedGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = GlassCyanBorder
            ) {
                if (!isOtpSent) {
                    Text(
                        text = "SYNC COGNITIVE KEY",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = TextStyle(
                            shadow = Shadow(color = CyberCyan, blurRadius = 8f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Username input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Quantum Signature (Name)", color = GlassTextSecondary) },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = CyberCyan) },
                        textStyle = TextStyle(color = GlassTextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            cursorColor = CyberCyan,
                            focusedContainerColor = Color(0x0AFFFFFF),
                            unfocusedContainerColor = Color(0x0AFFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Phone input
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Telepathic Comm Code (Phone)", color = GlassTextSecondary) },
                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = HologramBlue) },
                        textStyle = TextStyle(color = GlassTextPrimary),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HologramBlue,
                            unfocusedBorderColor = GlassWhiteBorder,
                            cursorColor = HologramBlue,
                            focusedContainerColor = Color(0x0AFFFFFF),
                            unfocusedContainerColor = Color(0x0AFFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_input"),
                        singleLine = true,
                        placeholder = { Text("+1 (2040) ...", color = Color.Gray) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = NeonPink,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    FrostedGlassButton(
                        onClick = {
                            if (nameInput.isBlank() || phoneInput.isBlank()) {
                                errorMessage = "Please establish terminal keys first."
                            } else {
                                errorMessage = null
                                isOtpSent = true
                            }
                        },
                        text = "REQUEST SYNAPSE OTP",
                        glowColor = CyberCyan,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "submit_login"
                    )

                } else {
                    Text(
                        text = "COGNITIVE CHALLENGE",
                        color = HologramBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = TextStyle(
                            shadow = Shadow(color = HologramBlue, blurRadius = 8f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "We've transmitted a holographic OTP challenge code to $phoneInput. Enter below to sync.",
                        color = GlassTextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // OTP input
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("Synaptic Validation Code", color = GlassTextSecondary) },
                        leadingIcon = { Icon(Icons.Filled.Security, contentDescription = null, tint = NeonPink) },
                        textStyle = TextStyle(color = GlassTextPrimary),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = GlassWhiteBorder,
                            cursorColor = NeonPink,
                            focusedContainerColor = Color(0x0AFFFFFF),
                            unfocusedContainerColor = Color(0x0AFFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_input"),
                        singleLine = true,
                        placeholder = { Text("Simulate code (e.g. 2040)", color = Color.Gray) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = NeonPink,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        FrostedGlassButton(
                            onClick = {
                                isOtpSent = false
                                otpInput = ""
                            },
                            text = "BACK",
                            glowColor = GlassTextSecondary,
                            modifier = Modifier.weight(1f),
                            testTag = "back_otp"
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        FrostedGlassButton(
                            onClick = {
                                if (otpInput.length < 4) {
                                    errorMessage = "Verification challenge failed. Must be 4+ symbols."
                                } else {
                                    onLoginSuccess(phoneInput, nameInput)
                                }
                            },
                            text = "SYNC & DECRYPT",
                            glowColor = CyberCyan,
                            modifier = Modifier.weight(1.5f),
                            testTag = "verify_otp"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Subtitle footer
            Text(
                text = "Secure Quantum Neural Protocol © 2040 Vibe Labs",
                color = Color(0x6694A3B8),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// Simple state flow helper since standard syntax differs slightly in pure composable
fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)
