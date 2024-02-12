package com.example.musicapp.presentation.screens.now_playing

import com.example.musicapp.data.cache.datastore.RepeatMode
import com.example.musicapp.data.models.Music

/**
 * Состояние UI для отображения экрана "Сейчас играет".
 *
 * @param isPlaying Флаг, указывающий на текущее состояние воспроизведения.
 * @param music Воспроизводимая музыка.
 * @param duration Длительность музыкальной композиции.
 * @param position Позиция воспроизведения.
 * @param showPlaylistItems Флаг, указывающий на отображение элементов плейлиста.
 * @param playlistItems Элементы плейлиста.
 * @param shuffleModeEnabled Флаг, указывающий на включенный режим перемешивания.
 * @param repeatMode Режим повтора воспроизведения.
 * @param isFavourite Флаг, указывающий на состояние "избранного".
 */

data class NowPlayingUiState(
    val isPlaying: Boolean = false,
    val music: Music = Music(title = "Now Playing"),
    val duration: Long = 0L,
    val position: Long = 0L,
    val showPlaylistItems: Boolean = false,
    val playlistItems: List<Music> = emptyList(),
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val isFavourite: Boolean = false
)