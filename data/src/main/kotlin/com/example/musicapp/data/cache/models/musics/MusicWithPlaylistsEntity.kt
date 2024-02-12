package com.example.musicapp.data.cache.models.musics

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.musicapp.data.cache.models.playlist.PlaylistEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicCrossRefEntity

/**
 * Data-класс, представляющий отношение между музыкальным треком и плейлистами в базе данных.
 *
 * @param playlistMusicEntity Встроенный объект PlaylistMusicCache, содержащий детали о музыкальном треке.
 * @param playlists Список объектов PlaylistCache, представляющих плейлисты, связанные с музыкальным треком.
 */

data class MusicWithPlaylistsEntity(
    @Embedded
    val playlistMusicEntity: PlaylistMusicEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PlaylistMusicCrossRefEntity::class, "music_id", "playlist_id")
    )
    val playlists: List<PlaylistEntity>
)
