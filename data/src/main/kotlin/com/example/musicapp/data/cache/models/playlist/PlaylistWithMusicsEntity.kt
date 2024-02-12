package com.example.musicapp.data.cache.models.playlist

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

/**
 * Data-класс, представляющий кешированную версию плейлиста с его связанными музыкальными треками.
 *
 * Этот класс используется для операций с базой данных Room, где необходимо извлечь плейлист
 * вместе со связанными с ним музыкальными треками.
 *
 * @param playlistEntity Кешированное представление плейлиста.
 * @param musics Список кешированных музыкальных треков, связанных с плейлистом.
 */

data class PlaylistWithMusicsEntity (
    @Embedded
    val playlistEntity: PlaylistEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(PlaylistMusicCrossRefEntity::class, "playlist_id", "music_id")
    )
    val musics: List<PlaylistMusicEntity>
)