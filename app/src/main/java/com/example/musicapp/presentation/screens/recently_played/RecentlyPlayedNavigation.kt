package com.example.musicapp.presentation.screens.recently_played

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Недавно прослушанные треки".
const val recentlyPlayedRoute = "recently_played_musics"

// Расширение для NavController, предоставляющее удобный метод для навигации на экран "Недавно прослушанные треки".
fun NavController.navigateToRecentlyPlayedMusics(){
    navigate(recentlyPlayedRoute)
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Недавно прослушанные треки".
 * @param onBack Обработчик нажатия кнопки "назад".
 */
fun NavGraphBuilder.recentlyPlayedMusicsScreen(onBack:() -> Unit){
    composable(recentlyPlayedRoute){
        // Создание экрана "Недавно прослушанные треки" с использованием Hilt для внедрения зависимостей.
        val viewModel: RecentlyPlayedViewModel = hiltViewModel()
        RecentlyPlayedMusicScreen(onBack = { onBack() }, viewModel = viewModel)
    }
}