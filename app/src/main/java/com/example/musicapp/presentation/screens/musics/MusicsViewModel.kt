package com.example.musicapp.presentation.screens.musics

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.MusicsSortOption
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.util.mapper.toMusic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными музыкальных треков.
 *
 * @property musicServiceConnection Связь с музыкальным сервисом для управления воспроизведением.
 * @property musicId Идентификатор музыкального трека.
 * @property musicDataStore Хранилище данных для настроек музыки.
 */


class MusicsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicId: String,
    private val musicDataStore: MusicDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicsUiState())
    val uiState: StateFlow<MusicsUiState> = _uiState.asStateFlow()

    // Объект [MediaBrowser], предоставляющий доступ к медиа-браузеру.
    private var mediaBrowser: MediaBrowser? = null

    // Инициализация класса. Здесь вызываются функции загрузки музыкальных треков,
    // текущего воспроизводимого трека и состояния воспроизведения.
    init {
        fetchMusics(musicId)
        fetchCurrentPlayingMusic()
        fetchPlayingState()
    }

    // Воспроизводит музыкальный трек с учетом текущих настроек сортировки и перемешивания.
    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val children = musicServiceConnection.getChildren(musicId).toMutableList()
        val mediaItems = when (_uiState.value.sortOption) {
            MusicsSortOption.TITLE -> children.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
            MusicsSortOption.ARTIST -> children.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
        }
        player.setMediaItems(mediaItems)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    // Воспроизводит музыкальный трек с перемешиванием.
    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val children = musicServiceConnection.getChildren(musicId).toMutableList()
        val mediaItems = when (_uiState.value.sortOption) {
            MusicsSortOption.TITLE -> children.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
            MusicsSortOption.ARTIST -> children.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
        }
        player.setMediaItems(mediaItems)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
    }

    // Воспроизводит или приостанавливает воспроизведение музыкального трека.
    // music: Музыкальный трек для воспроизведения или приостановки.
    fun playOrPause(music: Music) {
        val player = this.mediaBrowser ?: return
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE

        if (isPrepared && music.id.toString() == nowPlaying.mediaId) {
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                else -> player.play()
            }
        } else {
            val playlist: List<MediaItem> = musicServiceConnection.getChildren(musicId)
            val mediaItems = when (_uiState.value.sortOption) {
                MusicsSortOption.TITLE -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
                MusicsSortOption.ARTIST -> playlist.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
            }.toMutableList()
            val mediaItem = mediaItems.first { it.mediaId == music.id.toString() }

            if (mediaItems.isEmpty()) mediaItems.add(mediaItem)
            val indexOf = mediaItems.indexOf(mediaItem)
            val startIndex = if (indexOf >= 0) indexOf else 0
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Загружает музыкальные треки из репозитория и обновляет состояние UI.
    // musicId: Идентификатор музыкального трека.
    private fun fetchMusics(musicId: String) {
        _uiState.update {
            it.copy(loading = true)
        }

        // Запускаем сопрограмму для выполнения операции загрузки данных.
        viewModelScope.launch {
            combine(
                musicServiceConnection.mediaBrowser,
                musicDataStore.getMusicsSortOption()
            ) { browser, musicsSortOption ->
                browser to musicsSortOption
                // Обрабатываем исключение, если произошла ошибка в процессе получения данных.
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        musics = emptyList(),
                        error = throwable.localizedMessage ?: "Unknown Error",
                        loading = false
                    )
                }
            }.collect { (browser, musicsSortOption) ->
                this@MusicsViewModel.mediaBrowser = browser ?: return@collect
                val children = musicServiceConnection.getChildren(musicId)
                    .map { mediaItem -> mediaItem.toMusic() }
                // Сортируем список музыкальных треков в соответствии с выбранной опцией сортировки.
                val musics = when (musicsSortOption) {
                    MusicsSortOption.TITLE -> children.sortedBy { music -> music.title }
                    MusicsSortOption.ARTIST -> children.sortedBy { music -> music.artist }
                }
                _uiState.update {
                    it.copy(
                        musics = musics,
                        error = "",
                        loading = false,
                        sortOption = musicsSortOption
                    )
                }
            }
        }
    }

    // Загружает текущий воспроизводимый музыкальный трек и обновляет состояние UI.
    private fun fetchCurrentPlayingMusic() {
        viewModelScope.launch {
            musicServiceConnection.nowPlaying.collect { mediaItem ->
                if (mediaItem != MediaItem.EMPTY) {
                    _uiState.update {
                        it.copy(playingMusicId = mediaItem.mediaId)
                    }
                }
            }
        }
    }

    // Загружает состояние воспроизведения музыки и обновляет состояние UI.
    private fun fetchPlayingState() {
        viewModelScope.launch {
            musicServiceConnection.isPlaying.collect { isPlaying ->
                _uiState.update {
                    it.copy(isMusicPlaying = isPlaying)
                }
            }
        }
    }

    // Устанавливает опцию сортировки музыки в хранилище данных.
    // musicsSortOption: Опция сортировки музыки.
    fun setMusicsSortOption(musicsSortOption: MusicsSortOption) {
        viewModelScope.launch {
            musicDataStore.setMusicsSortOption(musicsSortOption)
        }
    }

    // Устанавливает режим перемешивания в хранилище данных.
    // shuffleMode: Режим перемешивания.
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }

    // Выполняется при завершении использования ViewModel.
    // Освобождает ресурсы и связи с сервисом воспроизведения музыки.
    override fun onCleared() {
        // Вызываем метод родительского класса для выполнения стандартных операций при завершении.
        super.onCleared()
        // Освобождаем связь с сервисом воспроизведения музыки.
        musicServiceConnection.releaseBrowser()
    }

    // Предоставляет фабрику для создания экземпляра ViewModel с указанным идентификатором музыки.
    // musicId: Идентификатор музыки.
    // @return Фабрика ViewModel.
    companion object {
        fun provideFactory(musicId: String) = viewModelFactory {
            initializer {
                // Получаем контекст из ключа приложения и создаем экземпляр ViewModel.
                val context = (this[APPLICATION_KEY] as Context).applicationContext
                MusicsViewModel(MusicServiceConnection(context), musicId, MusicDataStore(context))
            }
        }
    }
}