package com.example.musicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.musicapp.data.cache.service.ALBUMS_ID
import com.example.musicapp.data.cache.service.ARTISTS_ID
import com.example.musicapp.data.cache.service.MUSICS_ID
import com.example.musicapp.presentation.screens.album.album_details.albumDetailsScreen
import com.example.musicapp.presentation.screens.album.album_details.navigateToAlbumDetails
import com.example.musicapp.presentation.screens.album.albums.albumsScreen
import com.example.musicapp.presentation.screens.album.top_albums.navigateToTopAlbums
import com.example.musicapp.presentation.screens.album.top_albums.topAlbumsScreen
import com.example.musicapp.presentation.screens.artists.artist_details.artistDetailsScreen
import com.example.musicapp.presentation.screens.artists.artist_details.navigateToArtistDetails
import com.example.musicapp.presentation.screens.artists.artistsScreen
import com.example.musicapp.presentation.screens.favourites.favouriteMusicsScreen
import com.example.musicapp.presentation.screens.favourites.navigateToFavouriteMusics
import com.example.musicapp.presentation.screens.home.homeScreen
import com.example.musicapp.presentation.screens.home.navigateToHomeScreen
import com.example.musicapp.presentation.screens.musics.musicsScreen
import com.example.musicapp.presentation.screens.playlists.navigateToPlaylistScreen
import com.example.musicapp.presentation.screens.playlists.playlistMusics.navigateToPlaylistMusics
import com.example.musicapp.presentation.screens.playlists.playlistMusics.playlistMusicsScreen
import com.example.musicapp.presentation.screens.playlists.playlistsScreen
import com.example.musicapp.presentation.screens.recently_played.navigateToRecentlyPlayedMusics
import com.example.musicapp.presentation.screens.recently_played.recentlyPlayedMusicsScreen
import com.example.musicapp.presentation.screens.splash.splashRoute
import com.example.musicapp.presentation.screens.splash.splashScreen

/**
 * Функция для создания навигационного графа приложения.
 * Этот граф описывает навигацию между различными экранами приложения.
 * @param rootChildren Список корневых элементов медиа-контента.
 * @param navController Контроллер навигации.
 * @param modifier Модификатор для настройки внешнего вида навигационного графа.
 */

@Composable
fun AppNavGraph(
    rootChildren: List<MediaItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = splashRoute,
        modifier = modifier
    ) {
        splashScreen(
            navigateToHomeScreen = {
                navController.navigateToHomeScreen()
            }
        )

        homeScreen(
            navigateToPlaylistScreen = {
                navController.navigateToPlaylistScreen()
            },
            navigateToAlbumDetail = { albumId ->
                navController.navigateToAlbumDetails(albumId)
            },
            navigateToFavouriteMusics = {
                navController.navigateToFavouriteMusics()
            },
            navigateToTopAlbums = {
                navController.navigateToTopAlbums()
            },
            navigateToRecentlyPlayed = {
                navController.navigateToRecentlyPlayedMusics()
            }
        )
        musicsScreen(musicId = rootChildren.first { it.mediaId == MUSICS_ID }.mediaId)
        albumsScreen(
            albumsId = rootChildren.first { it.mediaId == ALBUMS_ID }.mediaId,
            navigateToAlbumDetails = { albumId ->
                navController.navigateToAlbumDetails(albumId)
            }
        )
        albumDetailsScreen(onBackPress = { navController.popBackStack() })
        artistsScreen(
            artistsId = rootChildren.first { it.mediaId == ARTISTS_ID }.mediaId,
            navigateToArtistDetails = { artistId ->
                navController.navigateToArtistDetails(artistId = artistId)
            }
        )
        artistDetailsScreen(
            navigateToAlbumDetails = { albumId ->
                navController.navigateToArtistDetails(albumId)
            },
            onBackPress = { navController.popBackStack() }
        )
        playlistsScreen(
            onBackPress = { navController.popBackStack() },
            navigateToPlaylistMusics = { playlistId ->
                navController.navigateToPlaylistMusics(
                    playlistId,
                    rootChildren.first { it.mediaId == MUSICS_ID }.mediaId
                )
            }
        )
        playlistMusicsScreen(onBackPress = { navController.popBackStack() })
        favouriteMusicsScreen(onBackPress = { navController.popBackStack() })
        topAlbumsScreen(
            navigateToAlbumDetails = { navController.navigateToAlbumDetails(it) },
            onBack = { navController.popBackStack() }
        )
        recentlyPlayedMusicsScreen(onBack = { navController.popBackStack() })
    }
}