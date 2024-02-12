package com.example.musicapp.presentation.screens.artists.components

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
import com.example.musicapp.data.cache.datastore.ArtistAlbumsSortOption
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Компонент выпадающего меню для сортировки альбомов артиста.
 *
 * @param expanded Флаг, определяющий раскрытие меню.
 * @param sortOption Опция сортировки.
 * @param onDismissRequest Функция для закрытия меню.
 * @param onSortAlbums Функция для установки опции сортировки альбомов.
 * @param modifier Модификатор компонента.
 */

@Composable
fun SortAlbumsDropDownMenu(
    expanded: Boolean,
    sortOption: String,
    onDismissRequest: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    val menuItems = ArtistAlbumsSortOption.values()

    DropdownMenu(expanded = expanded, onDismissRequest = { onDismissRequest() }) {
        menuItems.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (sortOption.lowercase() == item.name.lowercase()) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = modifier
                            .size(ExtraLargeSpacing)
                            .padding(start = MediumSpacing)
                    )
                }else{
                    Spacer(modifier = Modifier
                        .size(ExtraLargeSpacing)
                        .padding(start = MediumSpacing))
                }
                val menuItem = when(item){
                    ArtistAlbumsSortOption.TITLE -> "Title"
                        ArtistAlbumsSortOption.YEAR_ASCENDING -> "Year Ascending"
                    ArtistAlbumsSortOption.YEAR_DESCENDING -> "Year Descending"
                }
                DropdownMenuItem(text = { Text(text = menuItem) },
                    onClick = { onSortAlbums(item) })
            }
        }
    }
}