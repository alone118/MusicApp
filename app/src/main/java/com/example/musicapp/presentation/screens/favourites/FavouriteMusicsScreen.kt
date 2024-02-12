package com.example.musicapp.presentation.screens.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.cache.datastore.FavouriteMusicsSortOption
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.components.MediaControls
import com.example.musicapp.presentation.screens.favourites.components.FavouriteMusicsItem
import com.example.musicapp.presentation.screens.favourites.components.FavouritesSortMusicDropDownMenu
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Экран отображения избранных музыкальных треков.
 *
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param modifier Модификатор для настройки внешнего вида.
 * @param viewModel ViewModel для управления данными экрана.
 */

@Composable
fun FavouriteMusicsScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavouriteMusicsViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    LoadedFavouriteMusicsScreen(
        uiState = uiState,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        playOrPause = { viewModel.playOrPause(it.id.toString()) },
        onRemoveMusic = viewModel::removeMusic,
        onBackPress = { onBackPress() },
        onSortMusics = viewModel::setFavouriteMusicsSortOption,
        modifier = modifier
    )
}

/**
 * Загруженный экран избранных музыкальных треков.
 *
 * @param uiState Состояние UI для отображения избранных музыкальных треков.
 * @param play Функция для начала воспроизведения музыки.
 * @param shuffle Функция для перемешивания списка музыкальных треков.
 * @param playOrPause Функция для воспроизведения или приостановки музыкального трека.
 * @param onRemoveMusic Функция для удаления музыкального трека из избранных.
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param onSortMusics Функция для сортировки музыкальных треков.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun LoadedFavouriteMusicsScreen(
    uiState: FavouriteMusicsUiState,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (Music) -> Unit,
    onRemoveMusic: (Long) -> Unit,
    onBackPress: () -> Unit,
    onSortMusics: (FavouriteMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FavouriteMusicsHeader(
            sortOption = uiState.sortOption.name,
            onBackPress = onBackPress,
            onSortMusics = onSortMusics
        )
        SpacerHeight(ExtraLargeSpacing)
        MediaControls(
            play = { play() },
            shuffle = { shuffle() },
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
        Box(contentAlignment = Alignment.Center) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier = Modifier.height(ExtraLargeSpacing)) }
                items(items = uiState.musics, key = { it.id }) { music ->
                    FavouriteMusicsItem(
                        music = music,
                        playOrPause = playOrPause,
                        onRemoveMusic = onRemoveMusic
                    )
                }
            }
            if (!uiState.loading && uiState.musics.isEmpty()) EmptyScreen(
                textResourceId = R.string.no_favourite_musics,
            )
        }
    }
}

@Preview
@Composable
fun LoadedFavouriteMusicsScreenPreview() {
    LoadedFavouriteMusicsScreen(
        uiState = FavouriteMusicsUiState(),
        play = {},
        shuffle = {},
        playOrPause = {},
        onRemoveMusic = {},
        onBackPress = {},
        onSortMusics = {}
    )
}

/**
 * Заголовок экрана избранных музыкальных треков.
 *
 * @param sortOption Опция сортировки музыкальных треков.
 * @param onBackPress Функция для обработки нажатия кнопки "Назад".
 * @param onSortMusics Функция для сортировки музыкальных треков.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun FavouriteMusicsHeader(
    sortOption: String,
    onBackPress: () -> Unit,
    onSortMusics: (FavouriteMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expended by remember { mutableStateOf(false) }

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
            text = stringResource(id = R.string.favourites),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.padding(horizontal = MediumSpacing)) {
            IconButton(onClick = { expended = true }) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null
                )
            }
            FavouritesSortMusicDropDownMenu(
                expanded = expended,
                sortOption = sortOption,
                onDismissRequest = { expended = false },
                onSortOption = {
                    onSortMusics(it)
                    expended = false
                }
            )
        }
    }
}