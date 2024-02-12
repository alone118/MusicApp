package com.example.musicapp.domain.repositories

import com.example.musicapp.domain.models.PlaylistDomain
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс [PlaylistRepository] предоставляет методы для управления плейлистами.
 * Репозиторий отвечает за получение, вставку, обновление и удаление данных о плейлистах.
 */

interface PlaylistRepository {

    // Получение списка плейлистов в виде [Flow].
    fun getPlaylists(): Flow<List<PlaylistDomain>>

    // Получение конкретного плейлиста по его идентификатору в виде [Flow].
    // id: Идентификатор плейлиста.
    fun getPlaylist(id: Long): Flow<PlaylistDomain>

    // Асинхронно вставляет новый плейлист.
    // playlistDomain: Объект [PlaylistDomain], представляющий плейлист.
    suspend fun insertPlaylist(playlistDomain: PlaylistDomain)

    // Асинхронно обновляет информацию о существующем плейлисте.
    // playlistDomain: Объект [PlaylistDomain], представляющий обновленные данные плейлиста.
    suspend fun updatePlaylist(playlistDomain: PlaylistDomain)

    // Асинхронно удаляет плейлист по его идентификатору.
    // id: Идентификатор плейлиста для удаления.
    suspend fun deletePlaylist(id: Long)
}