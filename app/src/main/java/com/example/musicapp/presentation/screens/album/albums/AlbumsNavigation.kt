package com.example.musicapp.presentation.screens.album.albums

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Альбомы".
const val albumRoute = "albums"

/**
 * Расширение для NavGraphBuilder, определяющее экран "Альбомы".
 * @param albumsId Идентификатор альбома, который будет передан на экран "Альбомы".
 * @param navigateToAlbumDetails Функция для навигации на экран деталей альбома (принимает идентификатор альбома).
 */
fun NavGraphBuilder.albumsScreen(
    albumsId: String,
    navigateToAlbumDetails: (String) -> Unit
) {
    composable(albumRoute) {
        // Создание экрана "Альбомы" с переданным идентификатором альбома.
        AlbumsScreen(albumId = albumsId, navigateToAlbumsDetails = navigateToAlbumDetails)
    }
}