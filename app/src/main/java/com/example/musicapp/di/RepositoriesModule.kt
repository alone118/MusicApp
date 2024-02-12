package com.example.musicapp.di

import android.content.ContentResolver
import android.content.Context
import com.example.musicapp.data.cache.dao.FavouriteMusicsDao
import com.example.musicapp.data.cache.dao.PlaylistDao
import com.example.musicapp.data.cache.dao.PlaylistMusicsDao
import com.example.musicapp.data.cache.dao.RecentlyPlayedDao
import com.example.musicapp.data.cache.dao.TopAlbumsDao
import com.example.musicapp.data.repositories.AlbumsRepositoryImpl
import com.example.musicapp.data.repositories.ArtistRepositoryImpl
import com.example.musicapp.data.repositories.FavouriteMusicsRepositoryImpl
import com.example.musicapp.data.repositories.MusicRepositoryImpl
import com.example.musicapp.data.repositories.PlaylistMusicsRepositoryImpl
import com.example.musicapp.data.repositories.PlaylistRepositoryImpl
import com.example.musicapp.data.repositories.RecentlyPlayedRepositoryImpl
import com.example.musicapp.data.repositories.TopAlbumsRepositoryImpl
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.ArtistRepository
import com.example.musicapp.domain.repositories.FavouriteMusicsRepository
import com.example.musicapp.domain.repositories.MusicRepository
import com.example.musicapp.domain.repositories.PlaylistMusicsRepository
import com.example.musicapp.domain.repositories.PlaylistRepository
import com.example.musicapp.domain.repositories.RecentlyPlayedRepository
import com.example.musicapp.domain.repositories.TopAlbumsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Dagger для предоставления репозиториев.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    // Предоставляет ContentResolver для доступа к данным контента.
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver{
        return context.contentResolver
    }

    // Предоставляет репозиторий музыки.
    @Provides
    fun provideMusicRepository(
        contentResolver: ContentResolver
    ): MusicRepository{
        return MusicRepositoryImpl(contentResolver)
    }

    // Предоставляет репозиторий альбомов.
    @Provides
    fun provideAlbumsRepository(
        contentResolver: ContentResolver
    ): AlbumsRepository{
        return AlbumsRepositoryImpl(contentResolver)
    }

    // Предоставляет репозиторий исполнителей.
    @Provides
    fun provideArtistRepository(
        contentResolver: ContentResolver
    ): ArtistRepository{
        return ArtistRepositoryImpl(contentResolver)
    }

    // Предоставляет репозиторий плейлистов.
    @Provides
    fun providePlaylistRepository(
        playlistDao: PlaylistDao,
        playlistMusicsDao: PlaylistMusicsDao
    ): PlaylistRepository{
        return PlaylistRepositoryImpl(playlistDao, playlistMusicsDao)
    }

    // Предоставляет репозиторий музыки в плейлистах.
    @Provides
    fun providePlaylistMusicsRepository(
        playlistMusicsDao: PlaylistMusicsDao
    ): PlaylistMusicsRepository = PlaylistMusicsRepositoryImpl(playlistMusicsDao)

    // Предоставляет репозиторий топ альбомов.
    @Provides
    fun provideTopAlbumsRepository(
        topAlbumsDao: TopAlbumsDao
    ): TopAlbumsRepository = TopAlbumsRepositoryImpl(topAlbumsDao)

    // Предоставляет репозиторий недавно проигранных треков.
    @Provides
    fun provideRecentlyPlayedRepository(
        recentlyPlayedDao: RecentlyPlayedDao
    ): RecentlyPlayedRepository = RecentlyPlayedRepositoryImpl(recentlyPlayedDao)

    // Предоставляет репозиторий избранных треков.
    @Provides
    fun provideFavouriteMusicsRepository(
        favouriteMusicsDao: FavouriteMusicsDao
    ): FavouriteMusicsRepository = FavouriteMusicsRepositoryImpl(favouriteMusicsDao)
}