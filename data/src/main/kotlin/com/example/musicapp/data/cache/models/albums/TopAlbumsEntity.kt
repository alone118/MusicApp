package com.example.musicapp.data.cache.models.albums

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data-класс, представляющий информацию о популярном альбоме в базе данных.
 *
 * @param albumId Идентификатор альбома.
 * @param totalPlayCount Общее количество воспроизведений этого альбома.
 */

@Entity(tableName = "top_albums")
data class TopAlbumsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val albumId: Long,
    @ColumnInfo(name = "total_play_count")
    val totalPlayCount: Int
)