package com.example.musicapp.presentation.screens.artists

import com.example.musicapp.data.models.Artist

/**
 * Состояние UI для отображения списка артистов.
 *
 * @param artists Список артистов.
 * @param error Сообщение об ошибке.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 */

data class ArtistsUiState(
    val artists: List<Artist> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
