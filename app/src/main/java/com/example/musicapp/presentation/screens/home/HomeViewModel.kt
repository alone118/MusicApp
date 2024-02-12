package com.example.musicapp.presentation.screens.home

import android.media.MediaMetadataRetriever
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.mappers.toUi
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.repositories.AlbumsRepositoryImpl
import com.example.musicapp.data.repositories.MusicRepositoryImpl
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.MusicRepository
import com.example.musicapp.domain.repositories.RecentlyPlayedRepository
import com.example.musicapp.domain.repositories.TopAlbumsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными главного экрана.
 *
 * @property musicRepository Репозиторий для работы с музыкальными треками.
 * @property topAlbumsRepository Репозиторий для получения топовых альбомов.
 * @property recentlyPlayedRepository Репозиторий для получения недавно воспроизведенных треков.
 * @property albumsRepository Репозиторий для работы с альбомами.
 * @property musicServiceConnection Связь с музыкальным сервисом для управления воспроизведением.
 */

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val topAlbumsRepository: TopAlbumsRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val albumsRepository: AlbumsRepository,
    private val musicServiceConnection: MusicServiceConnection
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Инициализация класса. Здесь вызываются функции загрузки топовых альбомов и недавно воспроизведенных треков.
    init {
        fetchTopAlbums()
        fetchRecentlyPlayed()
    }

    // Загружает топовые альбомы из репозитория и обновляет состояние UI.
    private fun fetchTopAlbums() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            topAlbumsRepository.getTopAlbums()
                .collect { topAlbums ->
                    // Преобразовываем топовые альбомы в доменный слой и берем первые 10.
                    val albums = topAlbums.map {
                        (albumsRepository as AlbumsRepositoryImpl).albums.firstOrNull { albums ->
                            it.albumId == albums.id
                        }?.toUi() ?: return@collect
                    }.take(10)
                    _uiState.update {
                        it.copy(topAlbums = albums, error = "", loading = false)
                    }
                }
        }
    }

    // Загружает недавно воспроизведенные треки из репозитория и обновляет состояние UI.
    private fun fetchRecentlyPlayed() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            recentlyPlayedRepository.getRecentlyPlayed()
                .collect { recentlyPlayed ->
                    // Преобразовываем недавно воспроизведенные треки в музыкальные треки и берем первые 10.
                    val musics = recentlyPlayed.map {
                        (musicRepository as MusicRepositoryImpl).musics().firstOrNull { musics ->
                            it.musicId == musics.id
                        } ?: return@collect
                    }.take(10).map {
                        // Преобразовываем музыкальные треки в доменный слой.
                        Music(
                            id = it.id,
                            uri = it.uri,
                            title = it.title,
                            album = it.album,
                            artist = it.artist,
                            trackNumber = it.trackNumber,
                            artworkUri = it.uri,
                            artworkData = getArtworkData(it.path)
                        )
                    }
                    _uiState.update {
                        it.copy(recentlyPlayed = musics, error = "", loading = false)
                    }
                }
        }
    }

    // Воспроизводит или приостанавливает воспроизведение музыкального трека.
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

    // Получает данные обложки музыкального трека по пути к файлу.
    // path: Путь к файлу музыкального трека.
    // @return Данные обложки в виде массива байтов или null, если ошибка.
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