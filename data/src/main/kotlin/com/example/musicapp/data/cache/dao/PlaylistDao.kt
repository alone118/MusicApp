package com.example.musicapp.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musicapp.data.cache.models.playlist.PlaylistEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для работы с плейлистами в локальной базе данных.
 * Предоставляет методы для получения, вставки, обновления и удаления плейлистов.
 */

@Dao
interface PlaylistDao {

    // Получает список всех плейлистов из базы данных.
    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    // Получает информацию о плейлисте по его идентификатору.
    // id: Идентификатор плейлиста
    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getPlaylist(id: Long): Flow<PlaylistEntity>

    // Вставляет новый плейлист в базу данных.
    // playlistEntity: Сущность плейлиста для вставки
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    // Обновляет информацию о плейлисте в базе данных.
    // playlistEntity: Сущность плейлиста для обновления
    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    // Удаляет плейлист из базы данных по его идентификатору.
    // id: Идентификатор плейлиста для удаления
    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylist(id: Long)
}