package com.example.musicapp.domain.repositories

import com.example.musicapp.domain.models.FavouriteMusicsDomain
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс [FavouriteMusicsRepository] предоставляет методы для взаимодействия с избранными музыкальными композициями.
 * Репозиторий отвечает за доступ к данным об избранных музыкальных произведениях, их добавление, удаление и управление статусом "избранной".
 */

interface FavouriteMusicsRepository {

    // Получение списка избранных музыкальных композиций в виде [Flow].
    fun getFavouriteMusics(): Flow<List<FavouriteMusicsDomain>>

    // Проверка, является ли музыка с указанным идентификатором избранной.
    // musicId: Идентификатор музыкальной композиции.
    fun isFavourite(musicId: Long): Flow<Boolean>

    // Переключение статуса избранности для музыки с указанным идентификатором.
    // musicId: Идентификатор музыкальной композиции.
    suspend fun toggleFavourite(musicId: Long)

    // Добавление новой избранной музыкальной композиции.
    // favouriteMusicsDomain: Объект, представляющий избранную музыкальную композицию.
    suspend fun addFavouriteMusic(favouriteMusicsDomain: FavouriteMusicsDomain)

    // Удаление избранной музыкальной композиции по её идентификатору.
    // musicId: Идентификатор музыкальной композиции для удаления.
    suspend fun deleteFavouriteMusic(musicId: Long)
}