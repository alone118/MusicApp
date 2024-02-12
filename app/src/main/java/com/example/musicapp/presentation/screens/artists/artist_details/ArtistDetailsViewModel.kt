package com.example.musicapp.presentation.screens.artists.artist_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.cache.datastore.ArtistAlbumsSortOption
import com.example.musicapp.data.cache.datastore.MusicDataStore
import com.example.musicapp.data.cache.service.MusicServiceConnection
import com.example.musicapp.presentation.util.mapper.toAlbum
import com.example.musicapp.presentation.util.mapper.toArtist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для управления данными об деталях исполнителя.
 *
 * @property musicServiceConnection Связь с музыкальным сервисом для получения данных.
 * @property musicDataStore Хранилище данных о музыке для получения сортировки альбомов исполнителя.
 * @param savedStateHandle Обработчик сохраненного состояния для получения аргументов.
 */

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val musicDataStore: MusicDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArtistDetailsUiState())
    val uiState: StateFlow<ArtistDetailsUiState> = _uiState.asStateFlow()

    // Аргументы для деталей исполнителя.
    private val artistDetailsArg = ArtistDetailsArg(savedStateHandle)

    // Инициализация класса. Здесь вызывается функция загрузки данных об исполнителе.
    init {
        fetchArtistDetails()
    }

    // Запрашивает данные об исполнителе и его альбомах через подключение к музыкальному сервису и хранилищу данных.
    private fun fetchArtistDetails() {
        _uiState.update { it.copy(loading = true) }

        combine(
            musicServiceConnection.mediaBrowser,
            musicDataStore.getArtistAlbumsSortOption()
        ) { browser, sortOption ->
            browser to sortOption
        }.onEach { (browser, sortOption) ->
            // Если browser равен null, возвращаемся из onEach без выполнения остального кода.
            if (browser == null) return@onEach

            // Получаем информацию о текущем исполнителе.
            val mediaItem =
                musicServiceConnection.getMediaItem(checkNotNull(artistDetailsArg.artistId))
            val children =
                musicServiceConnection.getChildren(checkNotNull(artistDetailsArg.artistId))
                    .map { item -> item.toAlbum() }
            // Сортируем альбомы в соответствии с выбранной опцией сортировки.
            val albums = when (sortOption) {
                ArtistAlbumsSortOption.TITLE -> children.sortedBy { it.name }
                ArtistAlbumsSortOption.YEAR_ASCENDING -> children.sortedBy { it.year }
                ArtistAlbumsSortOption.YEAR_DESCENDING -> children.sortedByDescending { it.year }
            }
            _uiState.update {
                it.copy(
                    artist = mediaItem.toArtist().name,
                    albums = albums,
                    error = "",
                    loading = false,
                    sortOption = sortOption
                )
            }
        }.catch {throwable ->
            _uiState.update {
                it.copy(
                    artist = "",
                    albums = emptyList(),
                    error = throwable.localizedMessage ?: "Unknown Error.",
                    loading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    // Устанавливает опцию сортировки альбомов исполнителя.
    // artistAlbumsSortOption: Опция сортировки альбомов исполнителя.
    fun setArtistAlbumsSortOption(artistAlbumsSortOption: ArtistAlbumsSortOption) {
        viewModelScope.launch {
            musicDataStore.setArtistAlbumsSortOption(artistAlbumsSortOption)
        }
    }
}