package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.data.cache.datastore.PlaylistMusicsSortOption
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.theme.MusicAppTheme

/**
 * Компонент выпадающего меню для сортировки музыки в плейлисте.
 *
 * @param expanded Флаг, указывающий, раскрыто ли меню.
 * @param sortOption Текущая опция сортировки.
 * @param onDismissRequest Функция обратного вызова для скрытия меню.
 * @param onSortOption Функция обратного вызова для выбора опции сортировки.
 * @param modifier Модификатор для настройки внешнего вида компонента.
 */

@Composable
fun PlaylistMusicsSortDropDownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortOption: (PlaylistMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    // Получение списка опций сортировки из enum PlaylistMusicsSortOption и преобразование их в список строк
    val menuItems = PlaylistMusicsSortOption.values().map {
        it.name.lowercase().replaceFirstChar { char -> char.titlecase() }
    }
    // Отображение выпадающего меню с опциями сортировки
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest() },
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        // Перебор всех опций сортировки для отображения в меню
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Отображение иконки "Check", если текущая опция сортировки соответствует выбранной
                if (sortOption.lowercase() == item.lowercase()) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ExtraLargeSpacing)
                            .padding(start = MediumSpacing)
                    )
                } else {
                    // Пространство для выравнивания элементов
                    Spacer(
                        modifier = Modifier
                            .size(ExtraLargeSpacing)
                            .padding(start = MediumSpacing)
                    )
                }
                // Отображение пункта меню с текстом опции сортировки и установка обработчика нажатия
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = { onSortOption(PlaylistMusicsSortOption.valueOf(item.uppercase())) }
                )
            }
        }
    }
}


@Preview
@Composable
fun PlaylistMusicsSortDropDownMenuPreview() {
    MaterialTheme {
        PlaylistMusicsSortDropDownMenu(
            expanded = false,
            sortOption = "",
            onDismissRequest = { },
            onSortOption = {}
        )
    }
}