package com.example.musicapp.di

import android.content.Context
import androidx.room.Room
import com.example.musicapp.data.cache.MusicDatabase
import com.example.musicapp.data.cache.dao.FavouriteMusicsDao
import com.example.musicapp.data.cache.dao.PlaylistDao
import com.example.musicapp.data.cache.dao.PlaylistMusicsDao
import com.example.musicapp.data.cache.dao.RecentlyPlayedDao
import com.example.musicapp.data.cache.dao.TopAlbumsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Dagger для предоставления DAO (Data Access Object).
 */

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    // Предоставляет экземпляр базы данных музыки.
    @Provides
    fun providesMusicDatabase(
        @ApplicationContext context: Context
    ): MusicDatabase = Room.databaseBuilder(
        context,
        MusicDatabase::class.java,
        "music-database"
    ).build()

    // Предоставляет DAO для работы с плейлистами.
    @Provides
    fun providesPlaylistDao(database: MusicDatabase):PlaylistDao = database.playlistDao

    // Предоставляет DAO для работы с музыкой в плейлистах.
    @Provides
    fun providesPlaylistMusicsDao(database: MusicDatabase): PlaylistMusicsDao = database.playlistMusicsDao

    // Предоставляет DAO для работы с топ альбомами.
    @Provides
    fun providesTopAlbumDao(database: MusicDatabase): TopAlbumsDao = database.topAlbumsDao

    // Предоставляет DAO для работы с недавно проигранными треками.
    @Provides
    fun providesRecentlyPlayedDao(database: MusicDatabase): RecentlyPlayedDao = database.recentlyPlayedDao

    // Предоставляет DAO для работы с избранными треками.
    @Provides
    fun providesFavouriteMusicsDao(database: MusicDatabase): FavouriteMusicsDao = database.favouriteMusicsDao
}