package com.example.musicapp.presentation.screens.recently_played

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.repositories.MusicRepositoryImpl
import com.example.musicapp.domain.repositories.MusicRepository
import com.example.musicapp.domain.repositories.RecentlyPlayedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными о недавно воспроизведенных музыкальных треках.
 *
 * @param recentlyPlayedRepository Репозиторий для получения данных о недавно воспроизведенных треках.
 * @param musicsRepository Репозиторий для получения данных о музыкальных треках.
 * @param musicServiceConnection Сервисное подключение к проигрывателю музыки.
 * @param musicDataStore Хранилище данных о музыке.
 */

@HiltViewModel
class RecentlyPlayedViewModel @Inject constructor(
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val musicsRepository: MusicRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecentlyPlayedUiState())
    val uiState: StateFlow<RecentlyPlayedUiState> = _uiState.asStateFlow()

    // Инициализация: загрузка недавно воспроизведенных треков.
    init {
        fetchRecentlyPlayed()
    }

    // Загружает данные о недавно воспроизведенных музыкальных треках и обновляет [RecentlyPlayedUiState].
    private fun fetchRecentlyPlayed() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            recentlyPlayedRepository.getRecentlyPlayed()
                .collect { recentlyPlayed ->
                    val musics = recentlyPlayed.map {
                        (musicsRepository as MusicRepositoryImpl).musics().firstOrNull { music ->
                            it.musicId == music.id
                        } ?: return@collect
                    }.map {
                        Music(
                            id = it.id,
                            uri = it.uri,
                            title = it.title,
                            artist = it.artist,
                            album = it.album,
                            trackNumber = it.trackNumber,
                            artworkData = getArtworkData(it.path),
                            artworkUri = it.uri
                        )
                    }
                    _uiState.update {
                        it.copy(recentlyPlayed = musics, error = "", loading = false)
                    }
                }
        }
    }

    // Начинает воспроизведение всех недавно воспроизведенных треков в порядке очереди.
    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val recentlyPlayed = _uiState.value.recentlyPlayed.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(recentlyPlayed)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    // Включает режим случайного воспроизведения для всех недавно воспроизведенных треков
    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val recentlyPlayed = _uiState.value.recentlyPlayed.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(recentlyPlayed)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
    }

    // Воспроизводит или приостанавливает воспроизведение трека по идентификатору.
    // id: Идентификатор трека, который нужно воспроизвести или приостановить.
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
            val recentlyPlayed = _uiState.value.recentlyPlayed.map {
                musicServiceConnection.getMediaItem(it.id.toString())
            }.toMutableList()
            val mediaItem = recentlyPlayed.first { it.mediaId == id }
            val startIndex = recentlyPlayed.indexOf(mediaItem)
            player.setMediaItems(recentlyPlayed, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Устанавливает режим случайного воспроизведения.
    // shuffleMode: Режим случайного воспроизведения.
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }

    // Получает данные об изображении обложки альбома из указанного пути к файлу.
    // path: Путь к файлу изображения.
    // @return Массив байтов, представляющих изображение, или null, если изображение не найдено.
    private fun getArtworkData(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(path)
        } catch (e: Exception) {
            retriever.release()
            return null
        }
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}