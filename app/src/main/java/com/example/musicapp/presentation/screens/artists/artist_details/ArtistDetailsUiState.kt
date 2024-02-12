package com.example.musicapp.presentation.screens.artists.artist_details

import com.example.musicapp.data.cache.datastore.ArtistAlbumsSortOption
import com.example.musicapp.data.models.Album

/**
 * Состояние UI для отображения деталей артиста.
 *
 * @param artist Имя артиста.
 * @param albums Список альбомов артиста.
 * @param error Сообщение об ошибке.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 * @param sortOption Опция сортировки альбомов.
 */

data class ArtistDetailsUiState(
    val artist: String = "",
    val albums: List<Album> = emptyList(),
    val error: String = "",
    val loading: Boolean = false,
    val sortOption: ArtistAlbumsSortOption = ArtistAlbumsSortOption.TITLE
)