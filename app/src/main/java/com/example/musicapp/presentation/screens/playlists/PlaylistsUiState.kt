package com.example.musicapp.presentation.screens.playlists

import com.example.musicapp.data.models.Playlist

/**
 * Состояние UI для отображения экрана "Плейлисты".
 *
 * @param playlists Список плейлистов.
 * @param error Сообщение об ошибке.
 * @param loading Флаг загрузки данных.
 */

data class PlaylistsUiState(
    val playlists: List<Playlist> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
