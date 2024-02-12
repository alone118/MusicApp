package com.example.musicapp.data.repositories

import com.example.musicapp.data.cache.dao.PlaylistDao
import com.example.musicapp.data.cache.dao.PlaylistMusicsDao
import com.example.musicapp.data.cache.models.playlist.PlaylistEntity
import com.example.musicapp.data.mappers.toEntity
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.domain.models.PlaylistDomain
import com.example.musicapp.domain.repositories.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация интерфейса [PlaylistRepository], отвечающая за взаимодействие
 * с плейлистами и их музыкальными композициями через [PlaylistDao] и [PlaylistMusicsDao].
 *
 * @property playlistDao [PlaylistDao], используемый для доступа к базе данных плейлистов.
 * @property playlistMusicsDao [PlaylistMusicsDao], используемый для доступа к базе данных
 * связей между плейлистами и музыкальными композициями.
 */

class PlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistMusicsDao: PlaylistMusicsDao
) : PlaylistRepository {

    // Получает список всех плейлистов в виде потока данных [Flow].
    // Каждый плейлист преобразуется в [PlaylistDomain] с использованием функции расширения [toDomain].
    // @return [Flow] с списком [PlaylistDomain].
    override fun getPlaylists(): Flow<List<PlaylistDomain>> {
        return playlistDao.getPlaylists().map {
            it.map(PlaylistEntity::toDomain)
        }
    }

    // Получает конкретный плейлист по его ID в виде потока данных [Flow].
    // Плейлист преобразуется в [PlaylistDomain] с использованием функции расширения [toDomain].
    // @param id ID плейлиста, который нужно получить.
    // @return [Flow] с [PlaylistDomain].
    override fun getPlaylist(id: Long): Flow<PlaylistDomain> {
        return playlistDao.getPlaylist(id).map { playlistEntity ->
            playlistEntity.toDomain()
        }
    }

    // Вставляет новый плейлист в базу данных.
    // @param playlistDomain [PlaylistDomain], представляющий вставляемый плейлист.
    override suspend fun insertPlaylist(playlistDomain: PlaylistDomain) {
        playlistDao.insertPlaylist(playlistDomain.toEntity())
    }

    // Обновляет информацию о существующем плейлисте в базе данных.
    // @param playlistDomain [PlaylistDomain], представляющий обновленный плейлист.
    override suspend fun updatePlaylist(playlistDomain: PlaylistDomain) {
        playlistDao.updatePlaylist(playlistDomain.toEntity())
    }

    // Удаляет плейлист по его ID из базы данных. Также удаляет связи между этим плейлистом
    // и музыкальными композициями в базе данных [PlaylistMusicsDao]. Если музыкальная композиция
    // больше не привязана к другим плейлистам, она также удаляется из базы данных [PlaylistMusicsDao].
    // @param id ID плейлиста, который нужно удалить.
    override suspend fun deletePlaylist(id: Long) {
        // Получение списка музыкальных композиций, связанных с удаляемым плейлистом
        val musics = playlistMusicsDao.getPlaylistMusics(id).first().musics
        // Удаление связей между плейлистом и его музыкальными композициями
        musics.forEach {
            playlistMusicsDao.deletePlaylistMusic(id, it.id)
            // Проверка, привязана ли музыкальная композиция к другим плейлистам
            val playlists = playlistMusicsDao.getPlaylistsWithMusic(it.id).first().playlists
            // Если музыкальная композиция больше не привязана к другим плейлистам, удаляем её из базы данных
            if (playlists.isEmpty()) playlistMusicsDao.deletePlaylistMusic(it.id)
        }
        // Удаление самого плейлиста из базы данных
        playlistDao.deletePlaylist(id)
    }
}