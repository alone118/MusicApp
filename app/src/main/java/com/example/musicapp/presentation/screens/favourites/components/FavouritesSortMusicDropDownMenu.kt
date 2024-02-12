package com.example.musicapp.presentation.screens.favourites.components

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
import com.example.musicapp.data.cache.datastore.FavouriteMusicsSortOption
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Компонент выпадающего меню для сортировки избранных музыкальных треков.
 *
 * @param expanded Флаг, определяющий, раскрыто ли меню.
 * @param sortOption Текущий вариант сортировки.
 * @param onDismissRequest Функция для закрытия меню.
 * @param onSortOption Функция для выбора варианта сортировки.
 * @param modifier Модификатор компонента.
 */

@Composable
fun FavouritesSortMusicDropDownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortOption: (FavouriteMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    // Список вариантов сортировки
    val menuItems = FavouriteMusicsSortOption.values().map {
        it.name.lowercase().replaceFirstChar { char -> char.titlecase() }
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismissRequest() },
        modifier = modifier
    ) {
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Иконка "галочка", если текущий вариант сортировки соответствует элементу
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
                // Элемент выпадающего меню для выбора варианта сортировки
                DropdownMenuItem(
                    text = { Text(text = item) },
                    onClick = { onSortOption(FavouriteMusicsSortOption.valueOf(item.uppercase())) }
                )
            }
        }
    }
}