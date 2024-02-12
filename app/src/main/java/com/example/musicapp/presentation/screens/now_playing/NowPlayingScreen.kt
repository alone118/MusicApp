package com.example.musicapp.presentation.screens.now_playing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.screens.musics.MusicsUiState
import com.example.musicapp.presentation.screens.now_playing.components.PlaybackControls
import com.example.musicapp.presentation.screens.now_playing.components.PlaybackImage
import com.example.musicapp.presentation.screens.now_playing.components.PlaybackMetaData
import com.example.musicapp.presentation.screens.now_playing.components.PlaybackOptions
import com.example.musicapp.presentation.screens.now_playing.components.PlaylistItems
import com.example.musicapp.presentation.theme.LargeSpacing

/**
 * Экран воспроизведения текущего трека, отображающий информацию о треке,
 * управляющие элементы воспроизведения и список воспроизведения, если он активен.
 *
 * @param uiState текущее состояние воспроизведения, содержащее информацию о треке и настройках воспроизведения.
 * @param onPositionChange функция обратного вызова для изменения позиции воспроизведения.
 * @param playNextMusic функция для перехода к следующему треку.
 * @param playPreviousMusic функция для перехода к предыдущему треку.
 * @param playOrPause функция для воспроизведения или паузы трека.
 * @param play функция для начала воспроизведения конкретного трека по его идентификатору.
 * @param togglePlaylistItems функция для переключения отображения списка воспроизведения.
 * @param toggleShuffleMode функция для переключения режима случайного воспроизведения.
 * @param updateRepeatMode функция для обновления режима повтора воспроизведения.
 * @param toggleFavourite функция для добавления или удаления трека из избранных.
 * @param modifier модификатор для управления внешним видом экрана.
 */

@Composable
fun NowPlayingScreen(
    uiState: NowPlayingUiState,
    onPositionChange: (Float) -> Unit,
    playNextMusic: () -> Unit,
    playPreviousMusic: () -> Unit,
    playOrPause: () -> Unit,
    play: (String) -> Unit,
    togglePlaylistItems: () -> Unit,
    toggleShuffleMode: () -> Unit,
    updateRepeatMode: () -> Unit,
    toggleFavourite: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Если активен список воспроизведения, отображаем его
        if (uiState.showPlaylistItems) {
            PlaylistItems(
                musics = uiState.playlistItems,
                nowPlaying = uiState.music,
                play = play,
                uiState = MusicsUiState()
            )
            Spacer(modifier = Modifier.weight(2f))
        } else { // В противном случае отображаем элементы управления воспроизведением и информацию о треке
            PlaybackImage(music = uiState.music)
            Spacer(modifier = Modifier.weight(1f))
            SpacerHeight(LargeSpacing)

            PlaybackMetaData(music = uiState.music)
            SpacerHeight(LargeSpacing)

            PlaybackOptions(
                shuffleModeEnabled = uiState.shuffleModeEnabled,
                repeatMode = uiState.repeatMode,
                isFavourite = uiState.isFavourite,
                toggleShuffleMode = { toggleShuffleMode() },
                updateRepeatMode = { updateRepeatMode() },
                toggleFavourite = { toggleFavourite(uiState.music.id) }
            )
        }
        SpacerHeight(LargeSpacing)
        PlaybackControls(
            isPlaying = uiState.isPlaying,
            duration = uiState.duration,
            position = uiState.position,
            showPlaylistItems = uiState.showPlaylistItems,
            onPositionChange = onPositionChange,
            playNextMusic = { playNextMusic() },
            playPreviousMusic = { playPreviousMusic() },
            playOrPause = { playOrPause() },
            togglePlaylistItems = { togglePlaylistItems() }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}


@Preview
@Composable
fun NowPlayingScreenPreview(){
    NowPlayingScreen(
        uiState = NowPlayingUiState(),
        onPositionChange = {},
        playNextMusic = {},
        playPreviousMusic = {},
        playOrPause = {},
        play = {},
        togglePlaylistItems = {},
        toggleShuffleMode = {},
        updateRepeatMode = {},
        toggleFavourite = {}
    )
}