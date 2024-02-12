package com.example.musicapp.presentation.screens.artists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.presentation.util.mapper.toArtist
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
 * ViewModel для управления данными об исполнителях музыки.
 *
 * @property musicServiceConnection Связь с музыкальным сервисом для получения данных.
 * @property artistsId Идентификатор исполнителя, для которого загружаются данные.
 */


class ArtistsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val artistsId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistsUiState())
    val uiState: StateFlow<ArtistsUiState> = _uiState.asStateFlow()

    // Инициализация класса. Здесь вызывается функция загрузки данных об исполнителях.
    init {
        fetchArtists()
    }

    // Запрашивает данные об исполнителях через подключение к музыкальному сервису.
    private fun fetchArtists() {
        _uiState.update {
            it.copy(loading = true)
        }
        musicServiceConnection.mediaBrowser.onEach { browser ->
            // // Если browser равен null, возвращаемся из onEach без выполнения остального кода.
            if (browser == null) return@onEach
            val children = musicServiceConnection.getChildren(artistsId)
            _uiState.update {
                it.copy(
                    artists = children.map { mediaItem -> mediaItem.toArtist() },
                    error = "",
                    loading = false
                )
            }
        }.catch { throwable ->
            _uiState.update {
                it.copy(
                    artists = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error.",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        // Создает фабрику для [ArtistsViewModel].
        // artistsId: Идентификатор исполнителя, для которого создается ViewModel.
        // @return Фабрика для создания экземпляра [ArtistsViewModel].
        fun provideFactory(artistsId: String) = viewModelFactory {
            initializer {
                val context =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Context).applicationContext
                ArtistsViewModel(MusicServiceConnection(context), artistsId)
            }
        }
    }
}