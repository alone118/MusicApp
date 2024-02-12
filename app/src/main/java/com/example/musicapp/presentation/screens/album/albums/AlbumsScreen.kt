package com.example.musicapp.presentation.screens.album.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.screens.album.components.AlbumItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Экран отображения списка альбомов.
 *
 * @param albumId Идентификатор альбома.
 * @param navigateToAlbumsDetails Функция для перехода к деталям альбома.
 * @param modifier Модификатор для настройки отображения.
 */

@Composable
fun AlbumsScreen(
    albumId: String,
    navigateToAlbumsDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AlbumsViewModel = viewModel(factory = AlbumsViewModel.provideFactory(albumId))

    val uiState = viewModel.uiState.collectAsState()
    LoadedAlbumsScreen(
        uiState = uiState.value,
        navigateToAlbumsDetails = navigateToAlbumsDetails,
        modifier = modifier
    )
}

/**
 * Загруженный экран с данными альбомов.
 *
 * @param uiState Состояние UI для отображения.
 * @param navigateToAlbumsDetails Функция для перехода к деталям альбома.
 * @param modifier Модификатор для настройки отображения.
 */

@Composable
fun LoadedAlbumsScreen(
    uiState: AlbumsUiState,
    navigateToAlbumsDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Отображение экрана с состоянием по умолчанию, загрузки или ошибки
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading
    ) {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            Text(
                modifier = Modifier.padding(horizontal = ExtraLargeSpacing),
                text = stringResource(id = R.string.albums),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            SpacerHeight(MediumSpacing)
            // Отображение списка альбомов в сетке
            LazyVerticalGrid(
                columns = GridCells.Adaptive(128.dp),
                verticalArrangement = Arrangement.spacedBy(LargeSpacing),
                horizontalArrangement = Arrangement.spacedBy(ExtraLargeSpacing),
                contentPadding = PaddingValues(horizontal = ExtraLargeSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = uiState.albums, key = { it.id }) { album ->
                    AlbumItem(album = album, navigateToAlbumDetails = navigateToAlbumsDetails)
                }
            }
        }
        // Отображение сообщения о пустом списке альбомов, если список пуст и загрузка завершена
        if (!uiState.loading && uiState.albums.isEmpty()) EmptyScreen(R.string.no_found_albums)
    }
}

@Preview
@Composable
fun LoadedAlbumsScreenPreview() {
    LoadedAlbumsScreen(
        uiState = AlbumsUiState(),
        navigateToAlbumsDetails = {}
    )
}