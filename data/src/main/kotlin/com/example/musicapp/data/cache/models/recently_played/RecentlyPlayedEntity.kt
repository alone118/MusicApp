package com.example.musicapp.data.cache.models.recently_played

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data-класс, представляющий запись недавно воспроизведенных музыкальных треков в базе данных.
 *
 * @param musicsId Уникальный идентификатор музыкального трека.
 * @param time Время воспроизведения музыкального трека в формате времени Unix.
 */

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity (
    @PrimaryKey
    @ColumnInfo(name = "id")
    val musicsId: Long,
    @ColumnInfo(name = "time")
    val time: Long
)