package com.example.musicapp.presentation.screens.splash

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Заставка" (вводное окно).
const val splashRoute = "splash"

/**
 * Расширение для NavGraphBuilder, определяющее экран "Заставка".
 * @param navigateToHomeScreen Функция для навигации на экран "Главная".
 */
fun NavGraphBuilder.splashScreen(
    navigateToHomeScreen: () -> Unit
) {
    composable(splashRoute) {
        // Создание экрана "Заставка" с использованием Hilt для внедрения зависимостей.
        val viewModel: SplashViewModel = hiltViewModel()
        viewModel
        SplashScreen(
            navigateToHomeScreen = { navigateToHomeScreen() }
        )
    }
}