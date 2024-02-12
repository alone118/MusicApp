package com.example.musicapp.presentation.screens.now_playing

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.RepeatMode
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.repositories.AlbumsRepositoryImpl
import com.example.musicapp.domain.models.RecentlyPlayedDomain
import com.example.musicapp.domain.models.TopAlbumsDomain
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.FavouriteMusicsRepository
import com.example.musicapp.domain.repositories.RecentlyPlayedRepository
import com.example.musicapp.domain.repositories.TopAlbumsRepository
import com.example.musicapp.presentation.util.mapper.toMusic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными текущего воспроизводимого музыкального трека.
 *
 * @property musicServiceConnection Связь с музыкальным сервисом для управления воспроизведением.
 * @property musicDataStore Хранилище данных для настроек музыки.
 * @property topAlbumsRepository Репозиторий для работы с топовыми альбомами.
 * @property albumsRepository Репозиторий для работы с альбомами.
 * @property recentlyPlayedRepository Репозиторий для работы с недавно воспроизведенными треками.
 * @property favouriteMusicsRepository Репозиторий для работы с избранными музыкальными треками.
 */

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    private val topAlbumsRepository: TopAlbumsRepository,
    private val albumsRepository: AlbumsRepository,
    private val recentlyPlayedRepository: RecentlyPlayedRepository,
    private val favouriteMusicsRepository: FavouriteMusicsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NowPlayingUiState())
    val uiState: StateFlow<NowPlayingUiState> = _uiState.asStateFlow()

    // Обработчик для выполнения задач на основном потоке.
    // Используется для обновления позиции воспроизведения музыки.
    private val handler = Handler(Looper.getMainLooper())

    // Инициализация ViewModel, где отслеживаются изменения воспроизводимого музыкального трека,
    // его продолжительности, текущего состояния воспроизведения, режима перемешивания и режима повторения.
    init {
        // Обновление UI при изменении текущего воспроизводимого трека
        musicServiceConnection.nowPlaying.onEach { mediaItem ->
            if (mediaItem != MediaItem.EMPTY) {
                _uiState.update {
                    it.copy(music = mediaItem.toMusic())
                }
                fetchPlaylistItems()
                addTopAlbums(mediaItem)
                addRecentlyPlayed(mediaItem)
                fetchIsFavourite(mediaItem.mediaId.toLong())
            }
        }.launchIn(viewModelScope)
        musicServiceConnection.duration.onEach { duration ->
            _uiState.update {
                it.copy(duration = duration)
            }
            updatePosition()
        }.launchIn(viewModelScope)
        musicServiceConnection.isPlaying.onEach { isPlaying ->
            _uiState.update {
                it.copy(isPlaying = isPlaying)
            }
        }.launchIn(viewModelScope)
        getShuffleMode()
        getRepeatMode()
    }

    // Обновляет позицию воспроизведения музыкального трека в UI.
    private fun updatePosition() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        handler.postDelayed(object : Runnable {
            override fun run() {
                // Проверка, достигнут ли конец трека
                if (player.currentPosition == player.duration) {
                    // Сброс позиции до начала трека, если достигнут конец
                    _uiState.update {
                        it.copy(
                            position = 0L
                        )
                    }
                    return
                }
                _uiState.update {
                    it.copy(
                        position = player.currentPosition
                    )
                }
                // Повторная установка задачи на обновление через 100 миллисекунд
                handler.postDelayed(this, 100L)
            }
        }, 100L)
    }

    // Воспроизводит музыкальный трек с указанным идентификатором.
    // id: Идентификатор музыкального трека.
    fun play(id: String) {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val nowPlaying = musicServiceConnection.nowPlaying.value
        val isPrepared = player.playbackState != Player.STATE_IDLE
        val playlist = getPlaylistItems()
        if (isPrepared && id == nowPlaying.mediaId) {
            // Если музыкальный трек уже воспроизводится, то приостанавливаем воспроизведение.
            when {
                player.isPlaying -> player.pause()
                player.playbackState == Player.STATE_ENDED -> player.seekTo(C.TIME_UNSET)
                // Иначе воспроизводим.
                else -> player.play()
            }
        } else if (isPrepared) {
            // Если музыкальный трек еще не воспроизведен и проигрыватель готов, то выполняем seekTo для указанного трека.
            val mediaItem = playlist.first { it.mediaId == id }
            player.seekTo(getPlaylistItemWindowIndex(mediaItem), C.TIME_UNSET)
            if (!player.isPlaying) player.play()
        } else {
            // Если проигрыватель не готов, устанавливаем новый список музыкальных треков и воспроизводим первый трек.
            val mediaItem = playlist.first { it.mediaId == id }
            val startIndex = playlist.indexOf(mediaItem)
            player.setMediaItems(playlist, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Воспроизводит или приостанавливает текущий музыкальный трек.
    fun playOrPause() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        // Если музыка воспроизводится, приостанавливаем.
        if (player.isPlaying) {
            player.pause()
        } else {
            // Если музыка не воспроизводится, проверяем её статус.
            if (player.playbackState == Player.STATE_ENDED) {
                // Если музыка завершилась, возвращаемся в начало и обновляем позицию.
                player.seekTo(0)
                updatePosition()
                return
            }
            // В противном случае начинаем воспроизведение.
            player.play()
        }
    }

    // Воспроизводит следующий музыкальный трек в плейлисте.
    fun playNextMusic() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        player.seekToNextMediaItem()
    }

    // Воспроизводит предыдущий музыкальный трек в плейлисте.
    fun playPreviousMusic() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        player.seekToPreviousMediaItem()
    }

    // Перемотать воспроизведение к указанной позиции.
    // position: Позиция в миллисекундах.
    fun seekTo(position: Long) {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        // Если воспроизведение завершено, остановить автоматическое воспроизведение и обновить позицию.
        if (player.playbackState == Player.STATE_ENDED) {
            player.playWhenReady = false
            updatePosition()
        }
        // Установить позицию воспроизведения.
        player.seekTo(position)
    }

    // Переключить видимость плейлиста.
    // Если плейлист видим, то скрыть, и наоборот.
    fun togglePlaylistItems() {
        _uiState.update {
            it.copy(showPlaylistItems = !it.showPlaylistItems)
        }
    }

    // Включает или выключает режим перемешивания (Shuffle Mode).
    fun toggleShuffleMode() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        setShuffleMode(if (player.shuffleModeEnabled) ShuffleMode.OFF else ShuffleMode.ON)
    }

    // Переключает избранность музыкального трека.
    // musicId: Идентификатор музыкального трека.
    fun toggleFavourite(musicId: Long) {
        // Асинхронно переключаем состояние избранности музыкального трека.
        viewModelScope.launch {
            favouriteMusicsRepository.toggleFavourite(musicId)
            _uiState.tryEmit(_uiState.value.copy(isFavourite = !_uiState.value.isFavourite))
        }
    }

    // Обновляет режим повторения (Repeat Mode) в соответствии с текущим состоянием проигрывателя.
    fun updateRepeatMode() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val repeatMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.ALL
            Player.REPEAT_MODE_ALL -> RepeatMode.ONE
            else -> RepeatMode.OFF
        }
        setRepeatMode(repeatMode)
    }

    // Загрузка списка воспроизводимых треков и обновление UI состояния.
    private fun fetchPlaylistItems() {
        val playlist = getPlaylistItems()
        _uiState.update {
            it.copy(playlistItems = playlist.map { mediaItem -> mediaItem.toMusic() })
        }
    }

    // Получение списка воспроизводимых треков из музыкального сервиса.
    // @return Список воспроизводимых треков в виде объектов MediaItem.
    private fun getPlaylistItems(): List<MediaItem> {
        val player = musicServiceConnection.mediaBrowser.value ?: return emptyList()
        val playlist: MutableList<MediaItem> = mutableListOf()
        var windowIndex = player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled)
        // Итерация по окнам треков и добавление в плейлист
        for (index in 0 until player.currentTimeline.windowCount) {
            playlist.add(player.getMediaItemAt(windowIndex))
            windowIndex = player.currentTimeline.getNextWindowIndex(
                windowIndex,
                player.repeatMode,
                player.shuffleModeEnabled
            )
        }
        return playlist
    }

    // Получение индекса окна (позиции) трека в текущем плейлисте.
    // mediaItem: Трек, для которого нужно получить индекс.
    // @return Индекс окна (позиция) трека в текущем плейлисте.
    private fun getPlaylistItemWindowIndex(mediaItem: MediaItem): Int {
        val player = musicServiceConnection.mediaBrowser.value ?: return -1
        var windowIndex = player.currentTimeline.getFirstWindowIndex(player.shuffleModeEnabled)
        // Итерация по окнам треков для поиска индекса трека
        for (index in 0 until player.currentTimeline.windowCount) {
            if (player.getMediaItemAt(windowIndex) == mediaItem) return windowIndex
            windowIndex = player.currentTimeline.getNextWindowIndex(
                windowIndex,
                player.repeatMode,
                player.shuffleModeEnabled
            )
        }
        return player.currentMediaItemIndex
    }

    // Установка режима случайного воспроизведения треков.
    // shuffleMode: Режим случайного воспроизведения.
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
        }
    }

    // Получение текущего режима случайного воспроизведения и обновление UI состояния.
    private fun getShuffleMode() {
        viewModelScope.launch {
            musicDataStore.getShuffleMode().collect { shuffleMode ->
                _uiState.update {
                    it.copy(shuffleModeEnabled = shuffleMode == ShuffleMode.ON)
                }
            }
        }
    }

    // Установка режима повтора воспроизведения треков.
    // repeatMode: Режим повтора воспроизведения.
    private fun setRepeatMode(repeatMode: RepeatMode) {
        viewModelScope.launch {
            musicDataStore.setRepeatMode(repeatMode)
        }
    }

    // Получение текущего режима повтора воспроизведения и обновление UI состояния.
    private fun getRepeatMode() {
        viewModelScope.launch {
            musicDataStore.getRepeatMode().collect { repeatMode ->
                _uiState.update {
                    it.copy(repeatMode = repeatMode)
                }
            }
        }
    }

    // Добавление текущего трека в список топовых альбомов.
    // nowPlaying: Текущий воспроизводимый трек.
    private fun addTopAlbums(nowPlaying: MediaItem) {
        viewModelScope.launch {
            val list = (albumsRepository as AlbumsRepositoryImpl).albumMusics.values
            val albumId =
                list.firstOrNull { musics ->
                    musics.map { it.id }.contains(nowPlaying.mediaId.toLong())
                }?.first()?.albumId ?: return@launch
            val topAlbum = topAlbumsRepository.getTopAlbum(albumId)
            topAlbum?.let {
                topAlbumsRepository.addTopAlbum(TopAlbumsDomain(it.albumId, it.totalPlayCount + 1))
            } ?: topAlbumsRepository.addTopAlbum(TopAlbumsDomain(albumId, 1))
        }
    }

    // Добавление текущего трека в список недавно воспроизведенных треков.
    // nowPlaying: Текущий воспроизводимый трек.
    private fun addRecentlyPlayed(nowPlaying: MediaItem) {
        viewModelScope.launch {
            recentlyPlayedRepository.addRecentlyPlayed(
                RecentlyPlayedDomain(
                    musicId = nowPlaying.mediaId.toLong(),
                    time = System.currentTimeMillis()
                )
            )
        }
    }

    // Загружает информацию о том, является ли текущий музыкальный трек избранным.
    // musicId: Идентификатор музыкального трека.
    private fun fetchIsFavourite(musicId: Long) {
        viewModelScope.launch {
            favouriteMusicsRepository.isFavourite(musicId).collect { isFavourite ->
                _uiState.update {
                    it.copy(isFavourite = isFavourite)
                }
            }
        }
    }
}