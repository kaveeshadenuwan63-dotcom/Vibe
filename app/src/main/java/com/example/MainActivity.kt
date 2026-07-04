package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.MainHubScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.VibeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val vibeViewModel: VibeViewModel = viewModel()
                val currentScreen by vibeViewModel.currentScreen.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // High-tech Crossfade animations between Onboarding, Hub, and Chat screens
                    Crossfade(
                        targetState = currentScreen,
                        modifier = Modifier.fillMaxSize(),
                        label = "screen_navigation"
                    ) { screen ->
                        when (screen) {
                            is Screen.Onboarding -> {
                                OnboardingScreen(
                                    onLoginSuccess = { phone, name ->
                                        vibeViewModel.login(phone, name)
                                    }
                                )
                            }
                            is Screen.MainHub -> {
                                MainHubScreen(
                                    viewModel = vibeViewModel,
                                    onNavigateToChat = { chat ->
                                        vibeViewModel.navigateTo(Screen.ChatScreen(chat))
                                    }
                                )
                            }
                            is Screen.ChatScreen -> {
                                ChatScreen(
                                    viewModel = vibeViewModel,
                                    chat = screen.chat,
                                    onNavigateBack = {
                                        vibeViewModel.navigateTo(Screen.MainHub)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
