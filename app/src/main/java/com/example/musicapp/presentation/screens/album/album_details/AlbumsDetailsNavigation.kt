package com.example.musicapp.presentation.screens.album.album_details

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Ключ для передачи идентификатора альбома между компонентами.
internal const val albumIdArg = "albumId"

// Маршрут для навигации к экрану "Детали альбома".
const val albumDetailsRoute = "album_details"

/**
 * Класс, представляющий аргументы для навигации к экрану "Детали альбома".
 * @param albumId Идентификатор альбома.
 */
internal class AlbumsDetailsArgs(val albumId: String?) {
    constructor(savedStateHandle: SavedStateHandle) : this(savedStateHandle[albumIdArg])
}

/**
 * Расширение для NavController, предоставляющее удобный метод для навигации на экран "Детали альбома".
 * @param albumId Идентификатор альбома, который будет передан на экран "Детали альбома".
 */
fun NavController.navigateToAlbumDetails(albumId: String) {
    navigate("$albumDetailsRoute/$albumId")
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Детали альбома".
 * @param onBackPress Обработчик нажатия кнопки "назад".
 */
fun NavGraphBuilder.albumDetailsScreen(onBackPress: () -> Unit) {
    composable(
        route = "$albumDetailsRoute/${albumIdArg}",
        arguments = listOf(
            navArgument(albumIdArg) { type = NavType.StringType }
        )
    ) {
        // Создание экрана "Детали альбома" с использованием Hilt для внедрения зависимостей.
        val viewModel: AlbumDetailsViewModel = hiltViewModel()
        AlbumDetailsScreen(onBackPress = { onBackPress() }, viewModel = viewModel)
    }
}