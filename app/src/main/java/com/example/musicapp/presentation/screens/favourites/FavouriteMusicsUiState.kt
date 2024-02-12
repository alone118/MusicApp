package com.example.musicapp.presentation.screens.favourites

import com.example.musicapp.data.cache.datastore.FavouriteMusicsSortOption
import com.example.musicapp.data.models.Music

/**
 * Состояние UI для отображения списка избранных музыкальных композиций.
 *
 * @param musics Список избранных музыкальных композиций.
 * @param error Сообщение об ошибке.
 * @param loading Флаг, указывающий на состояние загрузки данных.
 * @param sortOption Вариант сортировки избранных музыкальных композиций.
 */

data class FavouriteMusicsUiState(
    val musics: List<Music> = emptyList(),
    val error: String = "",
    val loading: Boolean = false,
    val sortOption: FavouriteMusicsSortOption = FavouriteMusicsSortOption.TITLE
)
