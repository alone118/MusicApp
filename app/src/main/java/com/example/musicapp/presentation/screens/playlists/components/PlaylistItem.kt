package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.data.models.Playlist
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.util.PlaylistState

/**
 * Компонент, представляющий элемент списка плейлистов.
 *
 * @param playlist Плейлист для отображения.
 * @param onRenamePlaylist Функция для переименования плейлиста.
 * @param onDeletePlaylist Функция для удаления плейлиста.
 * @param navigateToPlaylistMusics Функция для перехода к музыке плейлиста.
 * @param modifier Модификатор компонента.
 */

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onRenamePlaylist: (Playlist) -> Unit,
    onDeletePlaylist: (Long) -> Unit,
    navigateToPlaylistMusics: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Используется для отслеживания состояния раскрытия меню
    var expended by remember { mutableStateOf(false) }
    // Используется для отслеживания состояния диалогового окна
    var showDialog by remember { mutableStateOf(false) }
    // Используется для хранения имени плейлиста в диалоговом окне
    var playlistName by remember { mutableStateOf(playlist.name) }

    Box(modifier = modifier.clickable { navigateToPlaylistMusics(playlist.id) }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ExtraLargeSpacing)
        ) {
            SpacerHeight(MediumSpacing)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.LightGray,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.size(56.dp)
                ) {
                    Image(
                        imageVector = Icons.Rounded.QueueMusic,
                        contentDescription = null,
                        modifier = Modifier.padding(LargeSpacing)
                    )
                }
                SpacerWight(MediumSpacing)
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = { expended = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null
                        )
                    }
                    PlaylistDropDownMenu(
                        expended = expended,
                        onDismissRequest = { expended = false },
                        onRenamePlaylist = {
                            expended = false
                            showDialog = true
                        },
                        onDeletePlaylist = {
                            onDeletePlaylist(playlist.id)
                            expended = false
                        }
                    )
                }
            }
            SpacerHeight(MediumSpacing)
            Divider(thickness = 0.5.dp)
        }
    }

    // Если showDialog равно true, показывается диалоговое окно для переименования плейлиста
    if (showDialog) {
        NewOrUpdatePlaylistsDialog(
            playerState = PlaylistState.UPDATE,
            name = playlistName,
            onNameChange = { playlistName = it },
            onDismissRequest = {
                showDialog = false
                playlistName = playlist.name
            },
            onCreatePlaylist = {
                onRenamePlaylist(Playlist(playlist.id, it))
                showDialog = false
                playlistName = it
            }
        )
    }
}


@Preview
@Composable
fun PlaylistItemPreview() {
    MaterialTheme{
        PlaylistItem(
            playlist = Playlist(1, "Barbie Girl"),
            onRenamePlaylist = {},
            onDeletePlaylist = {},
            navigateToPlaylistMusics = {}
        )
    }
}