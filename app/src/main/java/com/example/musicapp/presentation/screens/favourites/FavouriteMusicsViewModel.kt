package com.example.musicapp.presentation.screens.favourites

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.Player
import com.example.musicapp.data.cache.datastore.FavouriteMusicsSortOption
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.repositories.MusicRepositoryImpl
import com.example.musicapp.domain.repositories.FavouriteMusicsRepository
import com.example.musicapp.domain.repositories.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными о избранных музыкальных треках.
 *
 * @property favouriteMusicsRepository Репозиторий для работы с избранными музыкальными треками.
 * @property musicsRepository Репозиторий для работы с музыкальными треками.
 * @property musicServiceConnection Связь с музыкальным сервисом для управления воспроизведением.
 * @property musicDataStore Хранилище данных о музыке для получения опций сортировки и режима перемешивания.
 */

@HiltViewModel
class FavouriteMusicsViewModel @Inject constructor(
    private val favouriteMusicsRepository: FavouriteMusicsRepository,
    private val musicsRepository: MusicRepository,
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouriteMusicsUiState())
    val uiState: StateFlow<FavouriteMusicsUiState> = _uiState.asStateFlow()

    // Запуск функции загрузки избранных музыкальных треков при создании экземпляра ViewModel.
    init {
        fetchFavouriteMusics()
    }

    // Загружает избранные музыкальные треки из репозитория и обновляет состояние UI.
    private fun fetchFavouriteMusics() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            combine(
                favouriteMusicsRepository.getFavouriteMusics(),
                musicDataStore.getFavouriteMusicsSortOption()
            ) { favouriteMusics, sortOption ->
                val favMusics = favouriteMusics.map {
                    (musicsRepository as MusicRepositoryImpl).musics().firstOrNull { music ->
                        it.musicId == music.id
                    } ?: return@combine
                }.map {
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
                // Сортируем музыкальные треки в соответствии с выбранной опцией сортировки.
                val musics = when (sortOption) {
                    FavouriteMusicsSortOption.TITLE -> favMusics.sortedBy { it.title }
                    FavouriteMusicsSortOption.ARTIST -> favMusics.sortedBy { it.artist }
                }
                _uiState.update {
                    it.copy(musics = musics, error = "", loading = false, sortOption = sortOption)
                }
            }.launchIn(viewModelScope)
        }
    }

    // Воспроизводит избранные музыкальные треки в порядке добавления.
    fun play() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val favourites = _uiState.value.musics.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(favourites)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_OFF, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.OFF)
        player.prepare()
        player.play()
    }

    // Воспроизводит избранные музыкальные треки в режиме перемешивания.
    fun shuffle() {
        val player = musicServiceConnection.mediaBrowser.value ?: return
        val favourites = _uiState.value.musics.map {
            musicServiceConnection.getMediaItem(it.id.toString())
        }.toMutableList()
        player.setMediaItems(favourites)
        player.sendCustomCommand(MusicService.COMMAND_SHUFFLE_MODE_ON, Bundle.EMPTY)
        setShuffleMode(ShuffleMode.ON)
        player.prepare()
        player.play()
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
            val favourites = _uiState.value.musics.map {
                musicServiceConnection.getMediaItem(it.id.toString())
            }.toMutableList()
            val mediaItems = when (_uiState.value.sortOption) {
                FavouriteMusicsSortOption.TITLE -> favourites.sortedBy { mediaItem -> mediaItem.mediaMetadata.title.toString() }
                FavouriteMusicsSortOption.ARTIST -> favourites.sortedBy { mediaItem -> mediaItem.mediaMetadata.artist.toString() }
            }
            val mediaItem = mediaItems.first { it.mediaId == id }
            val startIndex = mediaItems.indexOf(mediaItem)
            player.setMediaItems(mediaItems, startIndex, C.TIME_UNSET)
            player.prepare()
            player.play()
        }
    }

    // Удаляет музыкальный трек из избранных.
    // musicId: Идентификатор музыкального трека.
    fun removeMusic(musicId: Long) {
        viewModelScope.launch {
            favouriteMusicsRepository.toggleFavourite(musicId)
        }
    }

    // Устанавливает опцию сортировки музыкальных треков в избранном.
    // favouriteMusicsSortOption: Опция сортировки музыкальных треков в избранном.
    fun setFavouriteMusicsSortOption(favouriteMusicsSortOption: FavouriteMusicsSortOption) {
        viewModelScope.launch {
            musicDataStore.setFavouriteMusicsSortOption(favouriteMusicsSortOption)
        }
    }

    // Устанавливает режим перемешивания в хранилище данных.
    // shuffleMode: Режим перемешивания.
    private fun setShuffleMode(shuffleMode: ShuffleMode) {
        viewModelScope.launch {
            musicDataStore.setShuffleMode(shuffleMode)
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