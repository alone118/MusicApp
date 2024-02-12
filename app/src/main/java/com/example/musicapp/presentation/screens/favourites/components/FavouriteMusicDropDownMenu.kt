package com.example.musicapp.presentation.screens.favourites.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.musicapp.R

/**
 * Компонент выпадающего меню для управления избранным музыкальным треком.
 *
 * @param expended Флаг, определяющий, раскрыто ли меню.
 * @param onDismissRequest Функция для закрытия меню.
 * @param onRemoveMusic Функция для удаления музыкального трека из избранных.
 * @param modifier Модификатор компонента.
 */

@Composable
fun FavouriteMusicDropDownMenu(
    expended: Boolean,
    onDismissRequest: () -> Unit,
    onRemoveMusic: () -> Unit,
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
                    text = stringResource(id = R.string.remove),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            },
            onClick = { onRemoveMusic() }
        )
    }
}