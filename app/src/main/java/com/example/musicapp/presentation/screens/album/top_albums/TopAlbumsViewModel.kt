package com.example.musicapp.presentation.screens.album.top_albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.mappers.toUi
import com.example.musicapp.data.repositories.AlbumsRepositoryImpl
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.TopAlbumsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для отображения списка популярных альбомов. Управляет состоянием UI и взаимодействует с репозиториями TopAlbumsRepository и AlbumsRepository.
 *
 * @param topAlbumsRepository Репозиторий для получения списка популярных альбомов.
 * @param albumsRepository Репозиторий для получения дополнительной информации об альбомах.
 */

@HiltViewModel
class TopAlbumsViewModel @Inject constructor(
    private val topAlbumsRepository: TopAlbumsRepository,
    private val albumsRepository: AlbumsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopAlbumsUiState())
    val uiState: StateFlow<TopAlbumsUiState> = _uiState.asStateFlow()

    // Инициализация ViewModel. Загрузка списка популярных альбомов.
    init {
        fetchTopAlbums()
    }

    // Загрузка списка популярных альбомов.
    private fun fetchTopAlbums() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
            topAlbumsRepository.getTopAlbums().collect { topAlbums ->
                val albums = topAlbums.map {
                    (albumsRepository as AlbumsRepositoryImpl).albums.first { album ->
                        it.albumId == album.id
                    }.toUi()
                }
                _uiState.update {
                    it.copy(topAlbums = albums, error = "", loading = false)
                }
            }
        }
    }
}