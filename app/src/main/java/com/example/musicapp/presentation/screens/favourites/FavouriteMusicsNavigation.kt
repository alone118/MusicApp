package com.example.musicapp.presentation.screens.favourites

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Маршрут для навигации к экрану "Избранные музыкальные треки".
const val favouriteMusicsRoute = "favourite_musics"

// Расширение для NavController, предоставляющее удобный метод для навигации на экран "Избранные музыкальные треки".
fun NavController.navigateToFavouriteMusics(){
    navigate(favouriteMusicsRoute)
}

/**
 * Расширение для NavGraphBuilder, определяющее экран "Избранные музыкальные треки".
 * @param onBackPress Обработчик нажатия кнопки "назад".
 */
fun NavGraphBuilder.favouriteMusicsScreen(onBackPress: () -> Unit){
    composable(route = favouriteMusicsRoute){
        // Создание экрана "Избранные музыкальные треки" с использованием Hilt для внедрения зависимостей.
        val viewModel:FavouriteMusicsViewModel = hiltViewModel()
        FavouriteMusicsScreen(onBackPress = { onBackPress() }, viewModel = viewModel)
    }
}