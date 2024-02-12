package com.example.musicapp.data.repositories

import com.example.musicapp.data.cache.dao.TopAlbumsDao
import com.example.musicapp.data.mappers.toEntity
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.domain.models.TopAlbumsDomain
import com.example.musicapp.domain.repositories.TopAlbumsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация интерфейса [TopAlbumsRepository], отвечающая за взаимодействие
 * с топовыми альбомами через [TopAlbumsDao].
 *
 * @property topAlbumsDao [TopAlbumsDao], используемый для доступа к базе данных топовых альбомов.
 */

class TopAlbumsRepositoryImpl @Inject constructor(
    private val topAlbumsDao: TopAlbumsDao
) : TopAlbumsRepository {

    // Получает список топовых альбомов в виде потока данных [Flow].
    // Каждый альбом преобразуется в [TopAlbumsDomain] с использованием функции расширения [toDomain].
    // @return [Flow] с списком [TopAlbumsDomain].
    override fun getTopAlbums(): Flow<List<TopAlbumsDomain>> {
        return topAlbumsDao.getTopAlbums().map { topAlbums ->
            topAlbums.map { it.toDomain() }
        }
    }

    // Получает конкретный топовый альбом по его ID в виде объекта [TopAlbumsDomain].
    // @param albumId ID топового альбома, который нужно получить.
    // @return [TopAlbumDomain] или null, если альбом не найден.
    override suspend fun getTopAlbum(albumId: Long): TopAlbumsDomain? {
        return topAlbumsDao.getTopAlbum(albumId)?.toDomain()
    }

    // Добавляет новый топовый альбом в базу данных.
    // @param topAlbum [TopAlbumDomain], представляющий добавляемый альбом.
    override suspend fun addTopAlbum(topAlbum: TopAlbumsDomain) {
        topAlbumsDao.addTopAlbum(topAlbum.toEntity())
    }

    // Удаляет топовый альбом из базы данных по его ID.
    // @param albumId ID топового альбома, который нужно удалить.
    override suspend fun deleteTopAlbum(albumId: Long) {
        topAlbumsDao.deleteTopAlbum(albumId)
    }
}