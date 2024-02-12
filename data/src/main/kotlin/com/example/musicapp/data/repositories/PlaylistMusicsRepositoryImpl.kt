package com.example.musicapp.data.repositories

import com.example.musicapp.data.cache.dao.PlaylistMusicsDao
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicCrossRefEntity
import com.example.musicapp.data.mappers.toEntity
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.domain.models.MusicDomain
import com.example.musicapp.domain.repositories.PlaylistMusicsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Реализация интерфейса [PlaylistMusicsRepository], отвечающая за взаимодействие
 * с музыкальными композициями в плейлистах через [PlaylistMusicsDao].
 *
 * @property playlistMusicsDao [PlaylistMusicsDao], используемый для доступа к базе данных
 * музыкальных композиций в плейлистах.
 */

class PlaylistMusicsRepositoryImpl(
    private val playlistMusicsDao: PlaylistMusicsDao
): PlaylistMusicsRepository {

    // Получает список музыкальных композиций из указанного плейлиста в виде потока данных [Flow].
    // Каждая композиция преобразуется в [MusicDomain] с использованием функции расширения [toDomain].
    // playlistId: ID плейлиста, из которого нужно получить композиции.
    // return: [Flow] с списком [MusicDomain].
    override fun getPlaylistMusics(playlistId: Long): Flow<List<MusicDomain>> {
        return playlistMusicsDao.getPlaylistMusics(playlistId).map {
            it.musics.map(PlaylistMusicEntity::toDomain)
        }
    }

    // Вставляет музыкальные композиции в указанный плейлист.
    // playlistId: ID плейлиста, в который нужно вставить композиции.
    // musicDomain: Массив объектов [MusicDomain], представляющих вставляемые композиции.
    override suspend fun insertPlaylistMusics(playlistId: Long, vararg musicDomain: MusicDomain) {
        // Вставка музыкальных композиций в таблицу музыкальных композиций плейлиста
        playlistMusicsDao.insertPlaylistMusics(*(musicDomain.map { it.toEntity() }.toTypedArray()))
        // Вставка связей между плейлистом и вставленными композициями
        playlistMusicsDao.insert(*(musicDomain.map { PlaylistMusicCrossRefEntity(playlistId, it.id) }.toTypedArray()))
    }

    // Удаляет музыкальную композицию из указанного плейлиста по их ID.
    // Если музыкальная композиция больше не привязана к другим плейлистам,
    // она также удаляется из таблицы музыкальных композиций.
    // playlistId: ID плейлиста, из которого нужно удалить композицию.
    // musicId: ID удаляемой музыкальной композиции.
    override suspend fun deletePlaylistMusic(playlistId: Long, musicId: Long) {
        // Удаление музыкальной композиции из таблицы музыкальных композиций плейлиста
        playlistMusicsDao.deletePlaylistMusic(playlistId, musicId)
        // Проверка, привязана ли музыкальная композиция к другим плейлистам
        val playlists = playlistMusicsDao.getPlaylistsWithMusic(musicId).first().playlists
        // Если музыкальная композиция больше не привязана к другим плейлистам, удаляем её из таблицы музыкальных композиций
        if (playlists.isEmpty()) playlistMusicsDao.deletePlaylistMusic(musicId)
    }
}