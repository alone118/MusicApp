package com.example.musicapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PeopleAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.musicapp.presentation.screens.album.albums.albumRoute
import com.example.musicapp.presentation.screens.artists.artistsRoute
import com.example.musicapp.presentation.screens.home.homeRoute
import com.example.musicapp.presentation.screens.musics.musicsRoute

/**
 * Sealed класс, представляющий элемент нижней навигационной панели приложения.
 * Каждый элемент представляет собой отдельную вкладку с иконкой, заголовком и маршрутом.
 * @param selectedIcon Иконка, отображаемая для выбранной вкладки.
 * @param unSelectedIcon Иконка, отображаемая для не выбранной вкладки.
 * @param title Заголовок вкладки.
 * @param route Маршрут, используемый для навигации к данной вкладке.
 */

sealed class BottomTab(
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val title: String,
    val route: String
){
    data object Home: BottomTab(
        selectedIcon = Icons.Default.Home,
        unSelectedIcon = Icons.Outlined.Home,
        title = "Home",
        route = homeRoute
    )

    data object Musics: BottomTab(
        selectedIcon = Icons.Default.MusicNote,
        unSelectedIcon = Icons.Outlined.MusicNote,
        title = "Musics",
        route = musicsRoute
    )

    data object Albums: BottomTab(
        selectedIcon = Icons.Default.Album,
        unSelectedIcon = Icons.Outlined.Album,
        title = "Albums",
        route = albumRoute
    )

    data object Artists: BottomTab(
        selectedIcon = Icons.Default.PeopleAlt,
        unSelectedIcon = Icons.Outlined.PeopleAlt,
        title = "Artists",
        route = artistsRoute
    )
}