package com.example.musicapp.domain.repositories

import com.example.musicapp.domain.models.MusicDomain
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс [PlaylistMusicsRepository] предоставляет методы для управления музыкальными композициями в плейлисте.
 * Репозиторий отвечает за получение, вставку и удаление данных о музыке в рамках конкретного плейлиста.
 */

interface PlaylistMusicsRepository {

    // Получение списка музыкальных композиций для указанного плейлиста в виде [Flow].
    // playlistId: Идентификатор плейлиста.
    fun getPlaylistMusics(playlistId: Long): Flow<List<MusicDomain>>

    // Асинхронно вставляет новые музыкальные композиции в указанный плейлист.
    // playlistId: Идентификатор плейлиста.
    // musicDomain: Переменное число объектов [MusicDomain], представляющих музыкальные композиции.
    suspend fun insertPlaylistMusics(playlistId: Long, vararg musicDomain: MusicDomain)

    // Асинхронно удаляет музыкальную композицию из указанного плейлиста.
    // playlistId: Идентификатор плейлиста.
    // musicId: Идентификатор музыкальной композиции для удаления.
    suspend fun deletePlaylistMusic(playlistId: Long, musicId: Long)
}