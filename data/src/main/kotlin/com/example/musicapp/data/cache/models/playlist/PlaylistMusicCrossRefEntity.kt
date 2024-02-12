package com.example.musicapp.data.cache.models.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Data-класс, представляющий кросс-ссылку между кешированным плейлистом и кешированным музыкальным треком.
 *
 * Кросс-ссылка используется для связи музыкальных треков с соответствующими плейлистами в базе данных Room.
 *
 * @param playlistId Идентификатор кешированного плейлиста.
 * @param musicId Идентификатор кешированного музыкального трека.
 */

@Entity(
    tableName = "playlists_musics",
    primaryKeys = ["playlist_id", "music_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class PlaylistMusicCrossRefEntity(
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long,
    @ColumnInfo(name = "music_id")
    val musicId: Long
)