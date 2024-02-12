package com.example.musicapp.presentation.screens.artists

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Исполнители".
const val artistsRoute = "artists"

/**
 * Расширение для NavGraphBuilder, определяющее экран "Исполнители".
 * @param artistsId Идентификатор исполнителя, который будет передан на экран "Исполнители".
 * @param navigateToArtistDetails Функция для навигации на экран деталей исполнителя (принимает идентификатор исполнителя).
 */
fun NavGraphBuilder.artistsScreen(
    artistsId: String,
    navigateToArtistDetails: (String) -> Unit
) {
    composable(artistsRoute){
        // Создание экрана "Исполнители" с переданным идентификатором исполнителя.
        ArtistsScreen(
            artistsId = artistsId,
            navigateToArtistDetails = navigateToArtistDetails,
        )
    }
}