package com.example.musicapp.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musicapp.data.cache.datastore.MusicsSortOption
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Компонент выпадающего меню для сортировки музыкальных треков.
 *
 * @param expanded Флаг, указывающий, раскрыто ли меню.
 * @param sortOption Выбранная опция сортировки.
 * @param onDismissRequest Функция обратного вызова для закрытия меню.
 * @param onSortOption Функция обратного вызова для выбора опции сортировки.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun SortMusicsDropDownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortOption: (MusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    // Создание списка элементов меню на основе доступных опций сортировки.
    val menuItems = MusicsSortOption.values().map {
        it.name.lowercase().replaceFirstChar { char -> char.titlecase() }
    }
    // Отображение выпадающего меню.
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest() },
        modifier = modifier
    ) {
        // Добавление каждой опции сортировки в меню.
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Отображение иконки "Галочки", если текущая опция совпадает с выбранной.
                if (sortOption.lowercase() == item.lowercase()) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(ExtraLargeSpacing)
                            .padding(start = MediumSpacing)
                    )
                } else {
                    // Пустое пространство, если текущая опция не выбрана.
                    Spacer(
                        modifier = Modifier
                            .size(ExtraLargeSpacing)
                            .padding(start = MediumSpacing)
                    )
                }
                // Добавление элемента меню с опцией сортировки.
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = { onSortOption(MusicsSortOption.valueOf(item.uppercase())) }
                )
            }
        }
    }
}