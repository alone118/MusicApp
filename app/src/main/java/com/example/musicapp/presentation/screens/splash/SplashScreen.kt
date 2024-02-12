package com.example.musicapp.presentation.screens.splash

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.musicapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Отображение сплеш экрана прилжения.
 *
 * @param modifier Модификатор для управления внешним видом заставки
 * @param navigateToHomeScreen Функция для перехода на главный экран после завершения заставки
 */

// Время задержки для экрана в миллисекундах.
private const val SPLASH_DELAY_TIME = 3_000L

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit
) {

    // Область корутины для управления задержкой и переходом на главный экран
    val scope = rememberCoroutineScope()

    // Скорость анимации заставки
    val speed by remember { mutableStateOf(1f) }

    // Загрузка композиции анимации из файла ресурсов
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_music_animation)
    )

    // Прогресс анимации
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        speed = speed,
        restartOnPlay = false
    )

    // Отображение компонентов заставки
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Отображение анимации
        LottieAnimation(
            modifier = Modifier.size(320.dp),
            composition = composition,
            progress = progress
        )
        // Задержка перед переходом на главный экран
        scope.launch {
            delay(SPLASH_DELAY_TIME)
            navigateToHomeScreen()
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(
            navigateToHomeScreen = {}
        )
    }
}