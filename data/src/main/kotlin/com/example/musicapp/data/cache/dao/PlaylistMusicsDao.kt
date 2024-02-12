package com.example.musicapp.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicapp.data.cache.models.musics.MusicWithPlaylistsEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicCrossRefEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistWithMusicsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для управления связью между плейлистами и музыкальными композициями в локальной базе данных.
 * Предоставляет методы для получения, вставки и удаления связей между плейлистами и музыкальными композициями.
 */

@Dao
interface PlaylistMusicsDao {

    // Получает все треки для указанного плейлиста.
    // playlistId: Идентификатор плейлиста
    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistMusics(playlistId:Long): Flow<PlaylistWithMusicsEntity>

    // Получает все плейлисты, содержащие указанный трек.
    // musicId: Идентификатор трека
    @Transaction
    @Query("SELECT * FROM playlist_musics WHERE id = :musicId")
    fun getPlaylistsWithMusic(musicId: Long): Flow<MusicWithPlaylistsEntity>

    // Вставляет новые связи между плейлистами и треками в базу данных.
    // join: Сущности, описывающие связь между плейлистом и треком
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg join: PlaylistMusicCrossRefEntity)

    // Вставляет новые треки для указанного плейлиста в базу данных.
    // entity: Сущности треков для вставки в плейлист
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistMusics(vararg entity: PlaylistMusicEntity)

    // Удаляет указанный трек из указанного плейлиста.
    // playlistId: Идентификатор плейлиста
    // musicId: Идентификатор трека
    @Query("DELETE FROM playlists_musics WHERE playlist_id = :playlistId AND music_id = :musicId")
    suspend fun deletePlaylistMusic(playlistId: Long, musicId: Long)

    // Удаляет указанный трек из всех плейлистов.
    // musicId: Идентификатор трека
    @Query("DELETE FROM playlist_musics WHERE id = :musicId")
    suspend fun deletePlaylistMusic(musicId: Long)
}