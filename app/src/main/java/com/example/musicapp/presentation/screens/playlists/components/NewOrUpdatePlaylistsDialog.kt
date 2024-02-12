package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.musicapp.R
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.util.PlaylistState

/**
 * Проверяет, доступна ли кнопка создания плейлиста на основе имени плейлиста.
 *
 * @param name Имя плейлиста.
 * @return true, если имя плейлиста допустимо для создания плейлиста, в противном случае false.
 */

private fun isCreateButtonEnabled(name: String): Boolean {
    return isPlaylistNameValid(name)
}

/**
 * Проверяет, является ли имя плейлиста допустимым для создания плейлиста.
 *
 * @param name Имя плейлиста.
 * @return true, если имя плейлиста допустимо, в противном случае false.
 */

private fun isPlaylistNameValid(name: String): Boolean {
    return name.isNotEmpty() && !name.endsWith(" ") && !name.startsWith(" ")
}

/**
 * Диалог для создания нового или обновления существующего плейлиста.
 *
 * @param playerState Состояние плейлиста (CREATE - создание, UPDATE - обновление).
 * @param name Имя плейлиста.
 * @param onNameChange Функция для изменения имени плейлиста.
 * @param onDismissRequest Функция для закрытия диалога.
 * @param onCreatePlaylist Функция для создания плейлиста.
 * @param modifier Модификатор компонента.
 */

@Composable
fun NewOrUpdatePlaylistsDialog(
    playerState: PlaylistState,
    name: String,
    onNameChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onCreatePlaylist: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    // Определение заголовка диалога в зависимости от состояния плейлиста
    val title = if (playerState == PlaylistState.CREATE) stringResource(id = R.string.new_playlist)
    else stringResource(id = R.string.update_playlist)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(MediumSpacing)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                SpacerHeight(MediumSpacing)
                TextField(
                    value = name,
                    onValueChange = { newValue ->
                        if (newValue.length <= 30 && !newValue.contains("\n")) {
                            onNameChange(newValue)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val trimmedName = name.trim()
                            if (isPlaylistNameValid(trimmedName)) {
                                val capitalized = trimmedName.substring(0, 1).toUpperCase() + trimmedName.substring(1)
                                println("Original: $name, Trimmed: $trimmedName, Capitalized: $capitalized")
                                onCreatePlaylist(capitalized)
                                onDismissRequest()
                            }
                        }
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.playlist_name),
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),

                    )
                SpacerHeight(MediumSpacing)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { onDismissRequest() }) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    Button(
                        onClick = {
                            val trimmedName = name.trim()
                            if (isPlaylistNameValid(trimmedName)) {
                                val capitalized = trimmedName.substring(0, 1).toUpperCase() + trimmedName.substring(1)
                                println("Original: $name, Trimmed: $trimmedName, Capitalized: $capitalized")
                                onCreatePlaylist(capitalized)
                                onDismissRequest()
                            }
                        },
                        enabled = isCreateButtonEnabled(name)
                    ) {
                        Text(text = stringResource(id = R.string.ok))
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun NewOrUpdatePlaylistsDialogPreview() {
    MaterialTheme {
        NewOrUpdatePlaylistsDialog(
            playerState = PlaylistState.UPDATE,
            name = "",
            onNameChange = {},
            onDismissRequest = {},
            onCreatePlaylist = {}
        )
    }
}