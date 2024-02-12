package com.example.musicapp.data.cache.models.favourites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data-класс, представляющий информацию о музыкальном треке, добавленном в избранное, в базе данных.
 *
 * @param musicId Идентификатор музыкального трека.
 */

@Entity(tableName = "favourite_musics")
data class FavouriteMusicEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val musicId: Long
)