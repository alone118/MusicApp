package com.example.musicapp.presentation.screens.musics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану музыки.
const val musicsRoute = "musics"

/**
 * Расширение для NavGraphBuilder, определяющее экран музыки.
 * @param musicId Идентификатор музыкального трека, который будет передан на экран музыки.
 */
fun NavGraphBuilder.musicsScreen(musicId: String){
    composable(musicsRoute){
        // Создание экрана музыки с переданным идентификатором музыкального трека.
        MusicsScreen(musicId = musicId)
    }
}