package com.example.musicapp.presentation.app

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.datastore.RepeatMode
import com.example.musicapp.data.cache.datastore.ShuffleMode
import com.example.musicapp.data.cache.service.MusicService
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel для управления данными в [MainActivity].
 *
 * @param musicServiceConnection Сервисное подключение к проигрывателю музыки.
 * @param musicDataStore Хранилище данных о музыке.
 */

// Состояние UI для [MainActivityViewModel], содержащее список корневых элементов библиотеки медиа.
// rootChildren: Список корневых элементов библиотеки медиа.
data class MainUiState(
    val rootChildren: List<MediaItem> = emptyList()
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    musicDataStore: MusicDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // Инициализация: отслеживание изменений в musicServiceConnection, musicDataStore.
    init {
        combine(
            musicServiceConnection.mediaBrowser,
            musicDataStore.getShuffleMode(),
            musicDataStore.getRepeatMode()
        ) { browser, shuffleMode, repeatMode ->
            val mediaBrowser: MediaBrowser = browser ?: return@combine
            // Получение информации о корне библиотеки медиа.
            val rootFuture = mediaBrowser.getLibraryRoot(null)
            // Обработка режима случайного воспроизведения и режима повтора.
            when (shuffleMode) {
                ShuffleMode.ON -> {
                    if (!mediaBrowser.shuffleModeEnabled) mediaBrowser.sendCustomCommand(
                        MusicService.COMMAND_SHUFFLE_MODE_ON,
                        Bundle.EMPTY
                    )
                }

                ShuffleMode.OFF -> {
                    if (mediaBrowser.shuffleModeEnabled) {
                        mediaBrowser.sendCustomCommand(
                            MusicService.COMMAND_SHUFFLE_MODE_OFF,
                            Bundle.EMPTY
                        )
                    }
                    when (repeatMode) {
                        RepeatMode.ONE -> mediaBrowser.sendCustomCommand(
                            MusicService.COMMAND_REPEAT_MODE_ONE,
                            Bundle.EMPTY
                        )

                        RepeatMode.ALL -> mediaBrowser.sendCustomCommand(
                            MusicService.COMMAND_REPEAT_MODE_ALL,
                            Bundle.EMPTY
                        )

                        RepeatMode.OFF -> mediaBrowser.sendCustomCommand(
                            MusicService.COMMAND_REPEAT_MODE_OFF,
                            Bundle.EMPTY
                        )
                    }
                }
            }
            // Обработка события по завершении загрузки информации о корне библиотеки медиа.
            rootFuture.addListener(
                {
                    val rootItem = rootFuture.get().value!!
                    fetchRootChildren(mediaBrowser, rootItem)
                },
                MoreExecutors.directExecutor()
            )
        }.launchIn(viewModelScope)
    }

    // Загружает дочерние элементы корня библиотеки медиа и обновляет [MainUiState].
    private fun fetchRootChildren(mediaBrowser: MediaBrowser, rootItem: MediaItem) {
        val childrenFuture = mediaBrowser.getChildren(rootItem.mediaId, 0, Int.MAX_VALUE, null)
        childrenFuture.addListener(
            {
                val result = childrenFuture.get()
                val children = result.value!!
                // Обновление UI с новым списком дочерних элементов.
                _uiState.update {
                    it.copy(rootChildren = children)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    // Освобождает ресурсы при завершении работы ViewModel.
    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.releaseBrowser()
    }
}