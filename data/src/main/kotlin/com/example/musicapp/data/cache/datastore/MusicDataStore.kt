package com.example.musicapp.data.cache.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Хранилище данных приложения, использующее [DataStore] для сохранения настроек.
 *
 * @property context Контекст приложения.
 */

// Расширение для доступа к [DataStore] в [Context].
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "music")

class MusicDataStore @Inject constructor(
    @ApplicationContext val context: Context,
) {
    // Ключи для сохранения и чтения настроек в [DataStore].
    private val musicsSortOptionKey = intPreferencesKey("musics_sort_option")
    private val playlistMusicsSortOptionLey = intPreferencesKey("playlist_musics_sort_option")
    private val artistAlbumsSortOptionKey = intPreferencesKey("artist_albums_sort_option")
    private val favouriteMusicsSortOptionKey = intPreferencesKey("favourite_musics_sort_option")
    private val shuffleModeKey = intPreferencesKey("shuffle_mode")
    private val repeatModeKey = intPreferencesKey("repeat_mode")

    // Устанавливает настройку сортировки для списка музыки.
    // musicsSortOption: Выбранная опция сортировки.
    suspend fun setMusicsSortOption(musicsSortOption: MusicsSortOption) {
        context.dataStore.edit { preference ->
            preference[musicsSortOptionKey] = musicsSortOption.ordinal
        }
    }

    // Устанавливает настройку сортировки для списка музыки в плейлисте.
    // playlistMusicsSortOption: Выбранная опция сортировки.
    suspend fun setPlaylistMusicsSortOption(playlistMusicsSortOption: PlaylistMusicsSortOption) {
        context.dataStore.edit { preference ->
            preference[playlistMusicsSortOptionLey] = playlistMusicsSortOption.ordinal
        }
    }

    // Устанавливает настройку сортировки для списка альбомов артиста.
    // artistAlbumsSortOption: Выбранная опция сортировки.
    suspend fun setArtistAlbumsSortOption(artistAlbumsSortOption: ArtistAlbumsSortOption) {
        context.dataStore.edit { preference ->
            preference[artistAlbumsSortOptionKey] = artistAlbumsSortOption.ordinal
        }
    }

    // Устанавливает настройку сортировки для списка избранных музыкальных треков.
    // favouriteMusicsSortOption: Выбранная опция сортировки.
    suspend fun setFavouriteMusicsSortOption(favouriteMusicsSortOption: FavouriteMusicsSortOption) {
        context.dataStore.edit { preference ->
            preference[favouriteMusicsSortOptionKey] = favouriteMusicsSortOption.ordinal
        }
    }

    // Устанавливает режим перемешивания.
    // shuffleMode: Выбранный режим перемешивания.
    suspend fun setShuffleMode(shuffleMode: ShuffleMode) {
        context.dataStore.edit { preference ->
            preference[shuffleModeKey] = shuffleMode.ordinal
        }
    }

    // Устанавливает режим повтора.
    // repeatMode: Выбранный режим повтора.
    suspend fun setRepeatMode(repeatMode: RepeatMode) {
        context.dataStore.edit { preference ->
            preference[repeatModeKey] = repeatMode.ordinal
        }
    }

    // Возвращает текущую настройку сортировки для списка музыки.
    fun getMusicsSortOption(): Flow<MusicsSortOption> {
        return context.dataStore.data.map { preference ->
            MusicsSortOption.values()[preference[musicsSortOptionKey]
                ?: MusicsSortOption.TITLE.ordinal]
        }
    }

    // Возвращает текущую настройку сортировки для списка музыки в плейлисте.
    fun getPlaylistMusicsSortOption(): Flow<PlaylistMusicsSortOption> {
        return context.dataStore.data.map { preference ->
            PlaylistMusicsSortOption.values()[preference[playlistMusicsSortOptionLey]
                ?: PlaylistMusicsSortOption.TITLE.ordinal]
        }
    }

    // Возвращает текущую настройку сортировки для списка альбомов артиста.
    fun getArtistAlbumsSortOption(): Flow<ArtistAlbumsSortOption> {
        return context.dataStore.data.map { preference ->
            ArtistAlbumsSortOption.values()[preference[artistAlbumsSortOptionKey]
                ?: ArtistAlbumsSortOption.TITLE.ordinal]
        }
    }

    // Возвращает текущую настройку сортировки для списка избранных музыкальных треков.
    fun getFavouriteMusicsSortOption(): Flow<FavouriteMusicsSortOption> {
        return context.dataStore.data.map { preference ->
            FavouriteMusicsSortOption.values()[preference[favouriteMusicsSortOptionKey]
                ?: FavouriteMusicsSortOption.TITLE.ordinal]
        }
    }

    // Возвращает текущий режим перемешивания.
    fun getShuffleMode(): Flow<ShuffleMode> {
        return context.dataStore.data.map { preference ->
            ShuffleMode.values()[preference[shuffleModeKey] ?: ShuffleMode.OFF.ordinal]
        }
    }

    // Возвращает текущий режим повтора.
    fun getRepeatMode(): Flow<RepeatMode> {
        return context.dataStore.data.map { preference ->
            RepeatMode.values()[preference[repeatModeKey] ?: RepeatMode.OFF.ordinal]
        }
    }
}