package com.example.musicapp.domain.repositories

import com.example.musicapp.domain.models.TopAlbumsDomain
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс [TopAlbumsRepository] предоставляет методы для управления топовыми альбомами.
 * Репозиторий отвечает за получение списка топовых альбомов, информации о конкретном топовом альбоме,
 * добавление нового топового альбома и удаление топового альбома.
 */

interface TopAlbumsRepository {

    // Получение списка топовых альбомов в виде [Flow].
    fun getTopAlbums(): Flow<List<TopAlbumsDomain>>

    // Асинхронно получает информацию о конкретном топовом альбоме.
    // albumId: Идентификатор топового альбома.
    // Объект [TopAlbumsDomain] с информацией о топовом альбоме или null, если альбом не найден.
    suspend fun getTopAlbum(albumId: Long): TopAlbumsDomain?

    // Асинхронно добавляет новый топовый альбом.
    // topAlbum: Объект [TopAlbumsDomain], представляющий топовый альбом.
    suspend fun addTopAlbum(topAlbum: TopAlbumsDomain)

    // Асинхронно удаляет топовый альбом по его идентификатору.
    // albumId: Идентификатор топового альбома для удаления.
    suspend fun deleteTopAlbum(albumId: Long)
}