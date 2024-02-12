package com.example.musicapp.presentation.screens.album.top_albums

import com.example.musicapp.data.models.Album

/**
 * Состояние UI на экране топовых альбомов.
 *
 * @param topAlbums Список топовых альбомов.
 * @param error Сообщение об ошибке.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 */

data class TopAlbumsUiState(
    val topAlbums: List<Album> = emptyList(),
    val error:String = "",
    val loading: Boolean = false
)
