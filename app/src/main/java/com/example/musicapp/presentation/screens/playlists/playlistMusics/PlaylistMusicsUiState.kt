package com.example.musicapp.presentation.screens.playlists.playlistMusics

import com.example.musicapp.data.cache.datastore.PlaylistMusicsSortOption
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.models.Playlist

/**
 * Состояние UI для отображения экрана "Музыка плейлиста".
 *
 * @param playlist Плейлист.
 * @param playlistMusics Список музыки плейлиста.
 * @param allMusics Все доступные музыкальные композиции.
 * @param selectedMusics Выбранные музыкальные композиции.
 * @param playingMusicId Идентификатор воспроизводимой музыкальной композиции.
 * @param error Сообщение об ошибке.
 * @param loading Флаг загрузки данных.
 * @param isMusicPlaying Флаг воспроизведения музыки.
 * @param sortOption Опция сортировки музыки плейлиста.
 */

data class PlaylistMusicsUiState(
    val playlist: Playlist = Playlist(-1L, ""),
    val playlistMusics: List<Music> = emptyList(),
    val allMusics: List<Music> = emptyList(),
    val selectedMusics: Set<Long> = emptySet(),
    val playingMusicId: String = "",
    val error: String = "",
    val loading: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val sortOption: PlaylistMusicsSortOption = PlaylistMusicsSortOption.TITLE
)
