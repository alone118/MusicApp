package com.example.musicapp.presentation.screens.album.album_details

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.presentation.util.mapper.toAlbum
import com.example.musicapp.presentation.util.mapper.toMusic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для отображения деталей альбома. Отвечает за управление состоянием UI и взаимодействие с сервисом музыкального проигрывателя.
 *
 * @param musicServiceConnection Экземпляр MusicServiceConnection для связи с сервисом музыкального проигрывателя.
 * @param musicDataStore Экземпляр MusicDataStore для работы с данными приложения, такими как режим перемешивания (shuffle).
 * @param savedStateHandle SavedStateHandle для сохранения и восстановления данных при изменении конфигурации устройства.
 */

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumDetailsUiState())
    val uiState: StateFlow<AlbumDetailsUiState> = _uiState.asStateFlow()

    private val albumsDetailsArgs = AlbumsDetailsArgs(savedStateHandle)

    // Инициализация ViewModel. Начальная загрузка данных о музыкальном альбоме и обновление состояния UI.
    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            musicServiceConnection.mediaBrowser.collect { browser ->
                // Если browser равен null, возвращаемся из collect без выполнения остального кода.
                if (browser == null) return@collect
                val item =
                    musicServiceConnection.getMediaItem(checkNotNull(albumsDetailsArgs.albumId))
                val children =
                    musicServiceConnection.getChildren(checkNotNull(albumsDetailsArgs.albumId))
                _uiState.update {
                    it.copy(
                        album = item.toAlbum(),
                        musics = children.map { mediaItem -> mediaItem.toMusic() },
                        loading = false
                    )
                }
            }
        }
    }

    // Запуск воспроизведения музыкального альбома.
    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val playlist: MutableList<MediaItem> =
            musicServiceConnection.getChildren(checkNotNull(albumsDetailsArgs.albumId))
                .toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    // Запуск воспроизведения музыкального альбома в режиме перемешивания.
    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val playlist: MutableList<MediaItem> =
            musicServiceConnection.getChildren(checkNotNull(albumsDetailsArgs.albumId))
                .toMutableList()
        player.setMediaItems(playlist)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
    }

    // Запуск воспроизведения или паузы для конкретного музыкального трека в альбоме.
    // id: Идентификатор музыкального трека.
    fun playOrPause(id: String) {
        val player = musicServiceConnection.mediaBrowser.value ?: return

        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE
        if (isPrepared && id == nowPlaying.mediaId) {
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                else -> player.play()
            }
        } else {
            val playlist: MutableList<MediaItem> =
                musicServiceConnection.getChildren(checkNotNull(albumsDetailsArgs.albumId))
                    .toMutableList()
            val mediaItem = playlist.firstOrNull { it.mediaId == id }
            val startIndex = playlist.indexOf(mediaItem)
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Установка режима перемешивания (shuffle mode) с использованием MusicDataStore.
    // shuffleMode: Режим перемешивания.
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode((shuffleMode))
        }
    }
}