package com.example.musicapp.presentation.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.mappers.toData
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.data.models.Playlist
import com.example.musicapp.domain.repositories.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления списком плейлистов.
 *
 * @param playlistRepository Репозиторий для получения данных о плейлистах.
 */

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlaylistsUiState())
    val uiState: StateFlow<PlaylistsUiState> = _uiState.asStateFlow()

    // Инициализация: загрузка списка плейлистов.
    init {
        fetchPlaylists()
    }

    // Загружает список плейлистов и обновляет [PlaylistsUiState].
    private fun fetchPlaylists() {
        _uiState.update {
            it.copy(loading = true)
        }
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    error = throwable.localizedMessage ?: "Unknown Error",
                    loading = false
                )
            }
        }
        viewModelScope.launch(coroutineExceptionHandler) {
            playlistRepository.getPlaylists().collect { playlists ->
                val playlistData = playlists.map { it.toData() }
                _uiState.update {
                    it.copy(
                        playlists = playlistData,
                        error = "",
                        loading = false
                    )
                }
            }
        }
    }

    // Сохраняет новый плейлист с указанным именем.
    // name: Имя нового плейлиста.
    fun savePlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.insertPlaylist(Playlist(0, name).toDomain())
        }
    }

    // Обновляет существующий плейлист.
    // playlist: Информация о плейлисте, которую нужно обновить.
    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(playlist.toDomain())
        }
    }

    // Удаляет плейлист с указанным идентификатором.
    // id: Идентификатор плейлиста, который нужно удалить.
    fun deletePlaylist(id: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(id)
        }
    }
}