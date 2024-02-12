package com.example.musicapp.presentation.screens.artists.artist_details

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Маршрут для навигации к экрану "Детали исполнителя".
const val artistDetailsRoute = "artist_details"
// Ключ для передачи идентификатора исполнителя между компонентами.
const val artistIdArg = "artistId"

/**
 * Класс, представляющий аргументы для навигации к экрану "Детали исполнителя".
 * @param artistId Идентификатор исполнителя.
 */
internal class ArtistDetailsArg(val artistId: String?) {
    constructor(savedStateHandle: SavedStateHandle) : this(savedStateHandle[artistIdArg])
}

/**
 * Расширение для NavController, предоставляющее удобный метод для навигации на экран "Детали исполнителя".
 * @param artistId Идентификатор исполнителя, который будет передан на экран "Детали исполнителя".
 */
fun NavController.navigateToArtistDetails(artistId: String) {
    navigate("$artistDetailsRoute/$artistId")
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Детали исполнителя".
 * @param navigateToAlbumDetails Функция для навигации на экран деталей альбома (принимает идентификатор альбома).
 * @param onBackPress Обработчик нажатия кнопки "назад".
 */
fun NavGraphBuilder.artistDetailsScreen(
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit
) {
    composable(
        route = "$artistDetailsRoute/{$artistIdArg}",
        arguments = listOf(
            navArgument(artistIdArg) { type = NavType.StringType }
        )
    ) {
        // Создание экрана "Детали исполнителя" с использованием Hilt для внедрения зависимостей.
        val viewModel: ArtistDetailsViewModel = hiltViewModel()
        ArtistDetailsScreen(
            navigateToAlbumDetails = { navigateToAlbumDetails },
            onBackPress = { onBackPress() },
            viewModel = viewModel
        )
    }
}