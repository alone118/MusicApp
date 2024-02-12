package com.example.musicapp.presentation.screens.album.top_albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.musicapp.R.string.top_albums
import com.example.musicapp.data.models.Album
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.screens.album.components.AlbumItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing

/**
 * Экран отображения списка топовых альбомов.
 *
 * @param navigateToAlbumsDetails Функция обратного вызова для навигации к деталям альбома.
 * @param onBack Функция обратного вызова для обработки нажатия кнопки назад.
 * @param modifier Модификатор для настройки отображения.
 * @param viewModel Экземпляр [TopAlbumsViewModel] для получения данных.
 */

@Composable
fun TopAlbumsScreen(
    navigateToAlbumsDetails: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TopAlbumsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ) {
        TopAlbumsContent(
            albums = uiState.topAlbums,
            navigateToAlbumsDetails = navigateToAlbumsDetails,
            onBack = { onBack() })
    }
}

/**
 * Контент экрана с топовыми альбомами.
 *
 * @param albums Список альбомов для отображения.
 * @param navigateToAlbumsDetails Функция обратного вызова для навигации к деталям альбома.
 * @param onBack Функция обратного вызова для обработки нажатия кнопки назад.
 * @param modifier Модификатор для настройки отображения.
 */

@Composable
fun TopAlbumsContent(
    albums: List<Album>,
    navigateToAlbumsDetails: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAlbumsHeader(onBack = { onBack() })
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),
            verticalArrangement = Arrangement.spacedBy(LargeSpacing),
            horizontalArrangement = Arrangement.spacedBy(ExtraLargeSpacing),
            contentPadding = PaddingValues(ExtraLargeSpacing),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = albums, key = { it.id }) { album ->
                AlbumItem(album = album, navigateToAlbumDetails = navigateToAlbumsDetails)
            }
        }
    }
}

/**
 * Заголовок экрана с топовыми альбомами.
 *
 * @param onBack Функция обратного вызова для обработки нажатия кнопки назад.
 * @param modifier Модификатор для настройки отображения.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAlbumsHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = top_albums),
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Icon(
                    modifier = modifier.size(28.dp),
                    imageVector = Icons.Rounded.ArrowBackIos,
                    contentDescription = null
                )
            }
        }
    )
}