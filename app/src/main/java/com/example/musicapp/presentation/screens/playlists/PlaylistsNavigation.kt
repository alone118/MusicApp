package com.example.musicapp.presentation.screens.playlists

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Плейлисты".
const val playlistsRoute = "playlists"

// Расширение для NavController, предоставляющее удобный метод для навигации на экран "Плейлисты".
fun NavController.navigateToPlaylistScreen() {
    navigate(playlistsRoute)
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Плейлисты".
 * @param onBackPress Обработчик нажатия кнопки "назад".
 * @param navigateToPlaylistMusics Функция для навигации на экран музыки плейлиста (принимает идентификатор плейлиста).
 */
fun NavGraphBuilder.playlistsScreen(
    onBackPress: () -> Unit,
    navigateToPlaylistMusics: (Long) -> Unit
) {
    composable(playlistsRoute) {
        // Создание экрана "Плейлисты" с использованием Hilt для внедрения зависимостей.
        val viewModel: PlaylistViewModel = hiltViewModel()
        PlaylistsScreen(
            onBackPress = { onBackPress() },
            navigateToPlaylistsMusics = navigateToPlaylistMusics,
            viewModel = viewModel
        )
    }
}