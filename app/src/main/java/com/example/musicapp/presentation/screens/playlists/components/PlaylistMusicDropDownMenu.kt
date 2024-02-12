package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.musicapp.R

/**
 * Компонент выпадающего меню для управления музыкой в плейлисте.
 *
 * @param expended Флаг, определяющий, раскрыто ли меню.
 * @param onDismissRequest Функция обратного вызова для закрытия меню.
 * @param onRemoveMusic Функция обратного вызова для удаления музыки из плейлиста.
 */

@Composable
fun PlaylistMusicDropDownMenu(
    expended: Boolean,
    onDismissRequest: () -> Unit,
    onRemoveMusic: () -> Unit,
) {
    DropdownMenu(
        expanded = expended,
        onDismissRequest = { onDismissRequest() }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.remove)) },
            onClick = { onRemoveMusic() }
        )
    }
}