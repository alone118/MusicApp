package com.example.musicapp.data.cache.models.playlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data-класс, представляющий плейлист в базе данных.
 *
 * @param id Уникальный идентификатор плейлиста (генерируется автоматически).
 * @param name Название плейлиста.
 */

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String
)
