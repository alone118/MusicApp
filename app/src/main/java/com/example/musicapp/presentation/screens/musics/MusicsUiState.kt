package com.example.musicapp.presentation.screens.musics

import com.example.musicapp.data.cache.datastore.MusicsSortOption
import com.example.musicapp.data.models.Music

/**
 * Состояние UI для отображения списка музыкальных композиций.
 *
 * @param musics Список музыкальных композиций.
 * @param playingMusicId Идентификатор воспроизводимой музыки.
 * @param error Сообщение об ошибке.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 * @param isMusicPlaying Флаг, указывающий на состояние воспроизведения музыки.
 * @param sortOption Опция сортировки списка композиций.
 */

data class MusicsUiState(
    val musics: List<Music> = emptyList(),
    val playingMusicId: String = "",
    val error: String = "",
    val loading: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val sortOption: MusicsSortOption = MusicsSortOption.TITLE
)
