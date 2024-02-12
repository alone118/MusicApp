package com.example.musicapp.presentation.screens.album.albums

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.presentation.util.mapper.toAlbum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * ViewModel для отображения списка альбомов. Отвечает за управление состоянием UI и взаимодействие с сервисом музыкального проигрывателя.
 *
 * @param musicServiceConnection Экземпляр MusicServiceConnection для связи с сервисом музыкального проигрывателя.
 * @param albumId Идентификатор альбома, для которого отображаются детали.
 */

class AlbumsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val albumId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlbumsUiState())
    val uiState: StateFlow<AlbumsUiState> = _uiState.asStateFlow()

    // Инициализация ViewModel. Загрузка списка альбомов для указанного идентификатора альбома.
    init {
        fetchAlbums()
    }

    // Загрузка списка альбомов для текущего идентификатора альбома.
    private fun fetchAlbums() {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            // Если browser равен null, возвращаемся из onEach без выполнения остального кода.
            if (browser == null) return@onEach
            val mediaItems = musicServiceConnection.getChildren(albumId)
            _uiState.update {
                it.copy(
                    albums = mediaItems.map { mediaItem -> mediaItem.toAlbum() },
                    error = "",
                    loading = false
                )
            }
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    albums = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        // Создает фабрику для создания экземпляра AlbumsViewModel с указанным идентификатором альбома.
        // albumId: Идентификатор альбома.
        // @return Фабрика для создания AlbumsViewModel.
        fun provideFactory(albumId: String) = viewModelFactory {
            initializer {
                val context =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context).applicationContext
                AlbumsViewModel(MusicServiceConnection(context), albumId)
            }
        }
    }
}