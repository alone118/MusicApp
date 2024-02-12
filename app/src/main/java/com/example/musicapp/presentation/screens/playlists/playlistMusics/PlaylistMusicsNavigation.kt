package com.example.musicapp.presentation.screens.playlists.playlistMusics

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Внутренние константы
internal const val playlistIdArg = "playlistId"
internal const val musicsRootArg = "musicsRoot"
const val playlistMusicsRoute = "playlist_musics"

// Класс, представляющий аргументы для навигации к экрану музыкального плейлиста.
internal class PlaylistMusicsArgs(val playlistId: Long?, val musicsRoot: String?) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        savedStateHandle[playlistIdArg],
        savedStateHandle[musicsRootArg]
    )
}

// Расширение для NavController, предоставляющее удобные методы навигации.
fun NavController.navigateToPlaylistMusics(playlistId: Long, musicsRoot: String) {
    navigate("$playlistMusicsRoute/$playlistId/$musicsRoot")
}

// Расширение для NavGraphBuilder, определяющее экран плейлиста с музыкой.
fun NavGraphBuilder.playlistMusicsScreen(onBackPress: () -> Unit) {
    composable(route = "$playlistMusicsRoute/{$playlistIdArg}/{$musicsRootArg}",
        arguments = listOf(
            navArgument(playlistIdArg) { type = NavType.LongType },
            navArgument(musicsRootArg) { type = NavType.StringType }
        )
    ) {
        // Создание ViewModel с использованием Hilt для внедрения зависимостей.
        val viewModel: PlaylistMusicsViewModel = hiltViewModel()
        PlaylistMusicsScreen(onBackPress = { onBackPress() }, viewModel = viewModel)
    }
}