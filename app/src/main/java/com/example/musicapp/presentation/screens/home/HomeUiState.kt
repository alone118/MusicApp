package com.example.musicapp.presentation.screens.home

import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Music
import com.example.musicapp.domain.models.AlbumDomain

/**
 * Состояние UI для отображения главного экрана.
 *
 * @param topAlbums Список топовых альбомов.
 * @param recentlyPlayed Список недавно проигранных композиций.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 * @param error Сообщение об ошибке.
 */

data class HomeUiState(
    val topAlbums: List<Album> = emptyList(),
    val recentlyPlayed: List<Music> = emptyList(),
    val loading: Boolean = false,
    val error: String = ""
)