package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.R

/**
 * Компонент выпадающего меню для плейлиста.
 *
 * @param expended Флаг, указывающий, раскрыто ли меню.
 * @param onDismissRequest Функция для закрытия меню.
 * @param onRenamePlaylist Функция для переименования плейлиста.
 * @param onDeletePlaylist Функция для удаления плейлиста.
 * @param modifier Модификатор компонента.
 */

@Composable
fun PlaylistDropDownMenu(
    expended: Boolean,
    onDismissRequest: () -> Unit,
    onRenamePlaylist: () -> Unit,
    onDeletePlaylist: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expended,
        onDismissRequest = { onDismissRequest() },
        modifier = modifier
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.rename),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            onClick = { onRenamePlaylist() },
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(id = R.string.delete),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            onClick = { onDeletePlaylist() }
        )
    }
}


@Preview
@Composable
fun PlaylistDropDownMenuPreview() {
    MaterialTheme{
        PlaylistDropDownMenu(
            expended = false,
            onDismissRequest = {},
            onRenamePlaylist = {},
            onDeletePlaylist = {}
        )
    }
}