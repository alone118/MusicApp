package com.example.musicapp.presentation.screens.recently_played

import com.example.musicapp.data.models.Music

/**
 * Состояние UI для отображения недавно воспроизведенной музыки.
 *
 * @param recentlyPlayed Список недавно воспроизведенной музыки.
 * @param error Сообщение об ошибке.
 * @param loading Флаг загрузки данных.
 */

data class RecentlyPlayedUiState(
    val recentlyPlayed: List<Music> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)