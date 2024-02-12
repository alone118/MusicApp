package com.example.musicapp.presentation.screens.artists.artist_details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Sort
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.cache.datastore.ArtistAlbumsSortOption
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.screens.album.components.AlbumItem
import com.example.musicapp.presentation.screens.artists.components.SortAlbumsDropDownMenu
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Экран отображения деталей об исполнителе.
 *
 * @param navigateToAlbumDetails Функция для перехода к деталям альбома.
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param viewModel Экземпляр [ArtistDetailsViewModel], предоставляющий данные для экрана.
 */

@Composable
fun ArtistDetailsScreen(
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit,
    viewModel: ArtistDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    LoadedArtistDetailsScreen(
        uiState = uiState,
        navigateToAlbumDetails = { navigateToAlbumDetails },
        onBackPress = { onBackPress() },
        onSortAlbums = viewModel::setArtistAlbumsSortOption
    )
}

/**
 * Экран отображения деталей об исполнителе с загруженными данными.
 *
 * @param uiState Состояние экрана с данными об исполнителе.
 * @param navigateToAlbumDetails Функция для перехода к деталям альбома.
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param onSortAlbums Функция для сортировки альбомов исполнителя.
 * @param modifier Модификатор для настройки отображения.
 */

@Composable
fun LoadedArtistDetailsScreen(
    uiState: ArtistDetailsUiState,
    navigateToAlbumDetails: (String) -> Unit,
    onBackPress: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    DefaultScreen(error = uiState.error, loading = uiState.loading) {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            ArtistAlbumsHeader(
                artist = uiState.artist,
                sortOption = uiState.sortOption.name,
                onBackPress = { onBackPress() },
                onSortAlbums = onSortAlbums
            )
            SpacerHeight(MediumSpacing)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                verticalArrangement = Arrangement.spacedBy(LargeSpacing),
                horizontalArrangement = Arrangement.spacedBy(ExtraLargeSpacing),
                contentPadding = PaddingValues(horizontal = ExtraLargeSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = uiState.albums, key = { it.id }) { album ->
                    AlbumItem(album = album, navigateToAlbumDetails = navigateToAlbumDetails)
                }
            }
        }
        if (!uiState.loading && uiState.albums.isEmpty()) EmptyScreen(
            textResourceId = R.string.no_found_albums_for_artist,
            modifier = Modifier.padding(ExtraLargeSpacing)
        )
    }
}

@Preview
@Composable
fun LoadedArtistDetailsScreenPreview() {
    LoadedArtistDetailsScreen(
        uiState = ArtistDetailsUiState(),
        navigateToAlbumDetails = {},
        onBackPress = {},
        onSortAlbums = {}
    )
}

/**
 * Заголовок списка альбомов исполнителя.
 *
 * @param artist Имя исполнителя.
 * @param sortOption Опция сортировки альбомов.
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param onSortAlbums Функция для сортировки альбомов.
 * @param modifier Модификатор для настройки отображения.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArtistAlbumsHeader(
    artist: String,
    sortOption: String,
    onBackPress: () -> Unit,
    onSortAlbums: (ArtistAlbumsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }

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
            text = artist,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee().weight(1f)
        )
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null
                )
            }
            SortAlbumsDropDownMenu(
                expanded = expanded,
                sortOption = sortOption,
                onDismissRequest = { expanded = false },
                onSortAlbums = {
                    onSortAlbums(it)
                    expanded = false
                }
            )
        }
    }
}