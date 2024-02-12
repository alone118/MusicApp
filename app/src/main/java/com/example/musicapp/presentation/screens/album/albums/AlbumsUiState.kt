package com.example.musicapp.presentation.screens.album.albums

import com.example.musicapp.data.models.Album

/**
 * Состояние UI для отображения списка альбомов.
 *
 * @param albums Список альбомов.
 * @param error Сообщение об ошибке.
 * @param loading Флаг загрузки данных.
 */

data class AlbumsUiState(
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)
