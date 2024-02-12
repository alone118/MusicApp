package com.example.musicapp.presentation.screens.playlists.playlistMusics

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.PlaylistMusicsSortOption
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.mappers.toData
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.data.mappers.toUi
import com.example.musicapp.domain.repositories.PlaylistMusicsRepository
import com.example.musicapp.domain.repositories.PlaylistRepository
import com.example.musicapp.presentation.extensions.addOrRemove
import com.example.musicapp.presentation.util.mapper.toMusic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными плейлиста и музыкальных треков внутри плейлиста.
 *
 * @property playlistRepository Репозиторий для работы с данными плейлистов.
 * @property playlistMusicsRepository Репозиторий для работы с данными о музыкальных треках внутри плейлиста.
 * @property musicServiceConnection Связь с музыкальным сервисом для управления воспроизведением.
 * @property musicDataStore Хранилище данных для настроек музыки.
 * @property savedStateHandle Обработчик сохраненного состояния ViewModel.
 */

@HiltViewModel
class PlaylistMusicsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val playlistMusicsRepository: PlaylistMusicsRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistMusicsUiState())
    val uiState: StateFlow<PlaylistMusicsUiState> = _uiState.asStateFlow()

    // Аргументы переданные через SavedStateHandle
    private val playlistMusicsArgs = PlaylistMusicsArgs(savedStateHandle)

    // Экземпляр MediaBrowser, используемый для взаимодействия с медиа-сервисом
    private var mediaBrowser: MediaBrowser? = null

    // Инициализация ViewModel. Запускает процессы получения данных о плейлисте, музыке внутри плейлиста и общей музыке.
    init {
        fetchPlaylist()
        fetchAllMusics()
        fetchPlaylistMusics()
    }

    // Загрузка плейлиста из репозитория и обновление UI
    private fun fetchPlaylist() {
        viewModelScope.launch {
            playlistRepository.getPlaylist(checkNotNull(playlistMusicsArgs.playlistId))
                .collect { playlist ->
                    _uiState.update {
                        it.copy(playlist = playlist.toData())
                    }
                }
        }
    }

    // Загрузка музыкальных треков плейлиста с учетом сортировки
    private fun fetchPlaylistMusics() {
        _uiState.update {
            it.copy(loading = true)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    playlistMusics = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error.",
                    loading = false
                )
            }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            combine(
                playlistMusicsRepository.getPlaylistMusics(checkNotNull(playlistMusicsArgs.playlistId)),
                musicDataStore.getPlaylistMusicsSortOption()
            ) { musics, playlistMusicsSortOption ->
                musics to playlistMusicsSortOption
            }.collect { (playlistMusics, playlistMusicsSortOption) ->
                val musics = when (playlistMusicsSortOption) {
                    PlaylistMusicsSortOption.TITLE -> playlistMusics.sortedBy { it.title }
                    PlaylistMusicsSortOption.ARTIST -> playlistMusics.sortedBy { it.artist }
                }.map { it.toUi() }
                _uiState.update {
                    it.copy(
                        playlistMusics = musics,
                        error = "",
                        loading = false,
                        sortOption = playlistMusicsSortOption
                    )
                }
            }
        }
    }

    // Загрузка всех музыкальных треков через MediaBrowser
    private fun fetchAllMusics() {
        viewModelScope.launch {
            musicServiceConnection.mediaBrowser.collect { browser ->
                this@PlaylistMusicsViewModel.mediaBrowser = browser ?: return@collect
                val children = getAllMusics()
                _uiState.update {
                    it.copy(allMusics = children.map { mediaItem -> mediaItem.toMusic() })
                }
            }
        }
    }

    // Получение всех музыкальных треков из MediaBrowser
    private fun getAllMusics(): List<MediaItem> {
        return musicServiceConnection.getChildren(checkNotNull(playlistMusicsArgs.musicsRoot))
    }

    // Вставка выбранных музыкальных треков в плейлист
    fun insertMusics() {
        viewModelScope.launch {
            val playlistId = _uiState.value.playlist.id
            val ids = _uiState.value.selectedMusics
            val musics = uiState.value.allMusics.filter { ids.contains(it.id) }
            playlistMusicsRepository.insertPlaylistMusics(
                playlistId,
                *musics.map { it.toDomain() }.toTypedArray()
            )
        }
    }

    // Обновление выбранных музыкальных треков
    fun updateSelectedMusics(musicId: Long, clean: Boolean = false) {
        if (clean) {
            _uiState.update {
                it.copy(selectedMusics = emptySet())
            }
            return
        }
        _uiState.update {
            val ids = it.selectedMusics.toMutableSet()
            ids.addOrRemove(musicId)
            it.copy(selectedMusics = ids)
        }
    }

    // Удаление музыкального трека из плейлиста
    fun removeMusic(musicId: Long) {
        viewModelScope.launch {
            val playlistId = _uiState.value.playlist.id
            playlistMusicsRepository.deletePlaylistMusic(playlistId, musicId)
        }
    }

    // Воспроизведение музыкального плейлиста
    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val ids = _uiState.value.playlistMusics.map { it.id.toString() }
        val playlist: MutableList<MediaItem> =
            getAllMusics().filter { ids.contains(it.mediaId) }.toMutableList()
        val mediaItems = when (_uiState.value.sortOption) {
            PlaylistMusicsSortOption.TITLE -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
            PlaylistMusicsSortOption.ARTIST -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
        }
        player.setMediaItems(mediaItems)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    // Воспроизведение музыкального плейлиста в случайном порядке
    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val ids = _uiState.value.playlistMusics.map { it.id.toString() }
        val playlist: MutableList<MediaItem> =
            getAllMusics().filter { ids.contains(it.mediaId) }.toMutableList()
        val mediaItems = when (_uiState.value.sortOption) {
            PlaylistMusicsSortOption.TITLE -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
            PlaylistMusicsSortOption.ARTIST -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
        }
        player.setMediaItems(mediaItems)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
    }

    // Воспроизведение или пауза музыкального трека
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
            val ids = _uiState.value.playlistMusics.map { it.id.toString() }
            val playlist: MutableList<MediaItem> =
                getAllMusics().filter { ids.contains(it.mediaId) }.toMutableList()
            val mediaItems = when (_uiState.value.sortOption) {
                PlaylistMusicsSortOption.TITLE -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
                PlaylistMusicsSortOption.ARTIST -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
            }
            val mediaItem = mediaItems.first { it.mediaId == id }
            val startIndex = mediaItems.indexOf(mediaItem)
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Установка сортировки для музыкальных треков в плейлисте
    fun setMusicsSortOption(playlistMusicsSortOption: PlaylistMusicsSortOption) {
        viewModelScope.launch {
            musicDataStore.setPlaylistMusicsSortOption(playlistMusicsSortOption)
        }
    }

    // Установка режима перемешивания для музыкальных треков в плейлисте
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }
}