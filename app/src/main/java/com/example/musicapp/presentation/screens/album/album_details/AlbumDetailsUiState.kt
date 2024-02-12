package com.example.musicapp.presentation.screens.album.album_details

import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Music

/**
 * Состояние UI на экране деталей альбома.
 *
 * @param album Информация об альбоме.
 * @param musics Список музыки в альбоме.
 * @param error Сообщение об ошибке, если произошла ошибка при загрузке данных.
 * @param loading Флаг, указывающий на то, выполняется ли загрузка данных.
 */

data class AlbumDetailsUiState(
    val album: Album = Album(-1L, "", "", "", "", 0, 0),
    val musics: List<Music> = emptyList(),
    val error: String = "",
    val loading: Boolean = false
)