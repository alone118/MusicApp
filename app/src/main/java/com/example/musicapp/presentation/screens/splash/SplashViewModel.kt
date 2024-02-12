package com.example.musicapp.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.example.musicapp.presentation.screens.home.navigateToHomeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "SplashScreen".
 * Инициализирует задержку и переход на следующий экран после указанного времени.
 */

// Время задержки для экрана "Заставка" в миллисекундах.
private const val SPLASH_DELAY_TIME = 2_000L

@HiltViewModel
class SplashViewModel @Inject constructor(
): ViewModel() {

    init {
        // Используется viewModelScope для корректного управления жизненным циклом ViewModel.
        viewModelScope.launch{
            // Задержка выполнения кода на SPLASH_DELAY_TIME в миллисекундах
            delay(SPLASH_DELAY_TIME)
        }
    }
}