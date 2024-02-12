package com.example.musicapp.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicapp.data.cache.dao.FavouriteMusicsDao
import com.example.musicapp.data.cache.dao.PlaylistDao
import com.example.musicapp.data.cache.dao.PlaylistMusicsDao
import com.example.musicapp.data.cache.dao.RecentlyPlayedDao
import com.example.musicapp.data.cache.dao.TopAlbumsDao
import com.example.musicapp.data.cache.models.favourites.FavouriteMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicCrossRefEntity
import com.example.musicapp.data.cache.models.recently_played.RecentlyPlayedEntity
import com.example.musicapp.data.cache.models.albums.TopAlbumsEntity
import com.example.musicapp.data.cache.util.ByteArrayConverter

/**
 * Класс базы данных Room для кеширования музыкальных данных.
 *
 * Этот класс предоставляет доступ к объектам доступа к данным (DAO) для работы
 * с кешированными плейлистами, музыкальными треками и связями между ними.
 *
 * @property playlistDao DAO для работы с кешированными плейлистами.
 * @property playlistMusicsDao DAO для работы с кешированными музыкальными треками.
 * @property topAlbumsDao DAO для работы с кешированными топовыми альбомами.
 * @property recentlyPlayedDao DAO для работы с кешированными недавно воспроизведенными треками.
 * @property favouriteMusicsDao DAO для работы с кешированными избранными музыкальными треками.
 */

@TypeConverters(ByteArrayConverter::class)
@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistMusicEntity::class,
        PlaylistMusicCrossRefEntity::class,
        TopAlbumsEntity::class,
        RecentlyPlayedEntity::class,
        FavouriteMusicEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class MusicDatabase : RoomDatabase() {
    abstract val playlistDao: PlaylistDao
    abstract val playlistMusicsDao: PlaylistMusicsDao
    abstract val topAlbumsDao: TopAlbumsDao
    abstract val recentlyPlayedDao: RecentlyPlayedDao
    abstract val favouriteMusicsDao: FavouriteMusicsDao
}