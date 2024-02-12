package com.example.musicapp.presentation.screens.album.top_albums

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Топовые альбомы".
const val topAlbumsRoute = "top_albums"

// Расширение для NavController, предоставляющее удобный метод для навигации на экран "Топовые альбомы".
fun NavController.navigateToTopAlbums() {
    navigate(topAlbumsRoute)
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Топовые альбомы".
 * @param navigateToAlbumDetails Функция для навигации на экран деталей альбома (принимает идентификатор альбома).
 * @param onBack Обработчик нажатия кнопки "назад".
 */
fun NavGraphBuilder.topAlbumsScreen(navigateToAlbumDetails: (String) -> Unit, onBack: () -> Unit) {
    composable(topAlbumsRoute) {
        // Создание экрана "Топовые альбомы" с использованием Hilt для внедрения зависимостей.
        val viewModel:TopAlbumsViewModel = hiltViewModel()
        TopAlbumsScreen(navigateToAlbumsDetails = navigateToAlbumDetails, onBack = { onBack() }, viewModel = viewModel)
    }
}