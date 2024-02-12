package com.example.musicapp.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapp.data.cache.models.favourites.FavouriteMusicEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для работы с избранными музыкальными композициями в локальной базе данных.
 * Предоставляет методы для получения, добавления, проверки наличия и удаления избранных музыкальных композиций.
 */

@Dao
interface FavouriteMusicsDao {

    // Получает список избранных музыкальных композиций в виде [Flow].
    @Query("SELECT * FROM favourite_musics")
    fun getFavouriteMusics(): Flow<List<FavouriteMusicEntity>>

    // Асинхронно добавляет новую избранную музыкальную композицию в базу данных.
    // favouriteMusicCache: Объект [FavouriteMusicCache], представляющий избранную музыкальную композицию.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavouriteMusic(favouriteMusicEntity: FavouriteMusicEntity)

    // Проверяет, является ли музыка с указанным идентификатором избранной.
    // musicId: Идентификатор музыкальной композиции.
    // return: [Flow] с булевым значением, указывающим, является ли музыка избранной.
    @Query("SELECT EXISTS(SELECT 1 FROM favourite_musics WHERE id =:musicId LIMIT 1)")
    fun isFavourite(musicId: Long): Flow<Boolean>

    // Асинхронно удаляет избранную музыкальную композицию из базы данных по её идентификатору.
    // musicId: Идентификатор музыкальной композиции для удаления избранной.
    @Query("DELETE FROM favourite_musics WHERE id = :musicId")
    suspend fun deleteFavouriteMusic(musicId: Long)
}