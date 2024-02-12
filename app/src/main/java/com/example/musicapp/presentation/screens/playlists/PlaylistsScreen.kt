package com.example.musicapp.presentation.screens.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.models.Playlist
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.screens.playlists.components.NewOrUpdatePlaylistsDialog
import com.example.musicapp.presentation.screens.playlists.components.PlaylistItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.util.PlaylistState

/**
 * Отображение экрана плейлистов.
 *
 * @param viewModel ViewModel для управления состоянием экрана
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param navigateToPlaylistsMusics Функция для перехода к экрану списка музыки в плейлисте
 * @param modifier Модификатор для управления внешним видом экрана
 */

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel,
    onBackPress: () -> Unit,
    navigateToPlaylistsMusics: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    // Получение состояния UI из ViewModel
    val uiState = viewModel.uiState.collectAsState().value
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
    ) {
        LoadedPlaylistsScreen(
            playlist = uiState.playlists,
            onCreatePlaylist = viewModel::savePlaylist,
            onRenamePlaylist = viewModel::updatePlaylist,
            onDeletePlaylist = viewModel::deletePlaylist,
            navigateToPlaylistsMusics = navigateToPlaylistsMusics,
            onBackPress = { onBackPress() }
        )

        // Отображение пустого экрана при отсутствии плейлистов
        if (!uiState.loading && uiState.playlists.isEmpty()) EmptyScreen(
            R.string.no_playlists, modifier = modifier.padding(
                ExtraLargeSpacing
            )
        )
    }
}

/**
 * Отображение загруженного экрана плейлистов.
 *
 * @param playlist Список плейлистов для отображения
 * @param onCreatePlaylist Функция для создания нового плейлиста
 * @param onRenamePlaylist Функция для переименования плейлиста
 * @param onDeletePlaylist Функция для удаления плейлиста
 * @param navigateToPlaylistsMusics Функция для перехода к экрану списка музыки в плейлисте
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param modifier Модификатор для управления внешним видом экрана
 */

@Composable
fun LoadedPlaylistsScreen(
    playlist: List<Playlist>,
    onCreatePlaylist: (String) -> Unit,
    onRenamePlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    navigateToPlaylistsMusics: (Long) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Отображение списка плейлистов
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        // Отображение заголовка экрана плейлистов
        PlaylistsScreenHeader(onCreatePlaylist = onCreatePlaylist,
            onBackPress = { onBackPress() }
        )
        // Отображение списка плейлистов
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(items = playlist, key = { it.id }) { playlist ->
                PlaylistItem(
                    playlist = playlist,
                    onRenamePlaylist = onRenamePlaylist,
                    onDeletePlaylist = onDeletePlaylist,
                    navigateToPlaylistMusics = navigateToPlaylistsMusics
                )
            }
        }
    }
}


@Preview
@Composable
fun LoadedPlaylistsScreenPreview() {
    MaterialTheme {
        LoadedPlaylistsScreen(
            playlist = listOf(
                Playlist(1, "Rock"),
                Playlist(2, "Jazz"),
                Playlist(3, "Pop"),
                Playlist(4, "Плейлист 1"),
            ),
            onCreatePlaylist = {},
            onRenamePlaylist = {},
            onDeletePlaylist = {},
            navigateToPlaylistsMusics = {},
            onBackPress = {}
        )
    }
}

/**
 * Отображение заголовка экрана плейлистов.
 *
 * @param onCreatePlaylist Функция для создания нового плейлиста
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param modifier Модификатор для управления внешним видом компонента
 */

@Composable
fun PlaylistsScreenHeader(
    onCreatePlaylist: (String) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние для ввода имени плейлиста
    var playlistName by remember { mutableStateOf("") }
    // Состояние для отображения диалогового окна
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onBackPress() }) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Rounded.ArrowBackIos,
                contentDescription = null
            )
        }
        Text(
            text = stringResource(id = R.string.playlists),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { showDialog = true }) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.Rounded.Add,
                contentDescription = null
            )
        }
    }

    // Отображение диалогового окна
    if (showDialog) {
        NewOrUpdatePlaylistsDialog(
            playerState = PlaylistState.CREATE,
            name = playlistName,
            onNameChange = { playlistName = it },
            onDismissRequest = {
                showDialog = false
                playlistName = ""
            },
            onCreatePlaylist = {
                onCreatePlaylist(it)
                showDialog = false
                playlistName = ""
            }
        )
    }
}