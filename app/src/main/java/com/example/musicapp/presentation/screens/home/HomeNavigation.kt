package com.example.musicapp.presentation.screens.home

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Главная".
const val homeRoute = "home"

// Расширение для NavController, предоставляющее удобный метод для навигации на экран "Главная".
fun NavController.navigateToHomeScreen(){
    navigate(homeRoute)
}
/**
 * Расширение для NavGraphBuilder, определяющее экран "Главная".
 * @param navigateToPlaylistScreen Функция для навигации на экран плейлистов.
 * @param navigateToAlbumDetail Функция для навигации на экран деталей альбома (принимает идентификатор альбома).
 * @param navigateToFavouriteMusics Функция для навигации на экран избранных музыкальных треков.
 * @param navigateToTopAlbums Функция для навигации на экран топовых альбомов.
 * @param navigateToRecentlyPlayed: Функция для навигации на экран недавно прослушанных треков.
 */
fun NavGraphBuilder.homeScreen(
    navigateToPlaylistScreen:() -> Unit,
    navigateToAlbumDetail: (String) -> Unit,
    navigateToFavouriteMusics:() -> Unit,
    navigateToTopAlbums:() -> Unit,
    navigateToRecentlyPlayed:() -> Unit
){
    composable(homeRoute){
        // Создание экрана "Главная" с использованием Hilt для внедрения зависимостей.
        val viewModel: HomeViewModel = hiltViewModel()
        HomeScreen(
            navigateToPlaylistsScreen = { navigateToPlaylistScreen() },
            navigateToAlbumDetail = navigateToAlbumDetail,
            navigateToFavouriteSongs = { navigateToFavouriteMusics() },
            navigateToTopAlbums = { navigateToTopAlbums() },
            navigateToRecentlyPlayed = { navigateToRecentlyPlayed() },
            viewModel = viewModel
        )
    }
}


