package com.example.musicapp.presentation.screens.musics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.data.cache.datastore.MusicsSortOption
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.components.MediaControls
import com.example.musicapp.presentation.components.MusicItem
import com.example.musicapp.presentation.components.SortMusicsDropDownMenu
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Отображение экрана с музыкальными композициями.
 * @param musicId Идентификатор музыкальной композиции.
 */

@Composable
fun MusicsScreen(
    musicId: String,
) {
    // Создание экземпляра ViewModel с использованием фабрики и получение состояния UI.
    val viewModel: MusicsViewModel = viewModel(factory = MusicsViewModel.provideFactory(musicId))
    val uiState by viewModel.uiState.collectAsState()

    LoadedMusicsScreen(
        uiState = uiState,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        playOrPause = viewModel::playOrPause,
        onSortOption = viewModel::setMusicsSortOption,
    )
}

/**
 * Отображение загруженного экрана с музыкальными композициями.
 * @param uiState Состояние UI для отображения музыкальных треков
 * @param play Функция для начала воспроизведения музыки.
 * @param shuffle Функция для перемешивания музыкального списка.
 * @param playOrPause Функция для начала воспроизведения или паузы музыки.
 * @param onSortOption Функция для установки опции сортировки музыкального списка.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun LoadedMusicsScreen(
    uiState: MusicsUiState,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (Music) -> Unit,
    onSortOption: (MusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
    ) {
        Column(
            modifier = modifier.background(MaterialTheme.colorScheme.background)
        ) {
            MusicScreenHeader(
                sortOption = uiState.sortOption.name,
                onSortOption = onSortOption
            )
            SpacerHeight(MediumSpacing)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Column {
                        MediaControls(
                            play = { play() },
                            shuffle = { shuffle() },
                            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
                        )
                        SpacerHeight(MediumSpacing)
                    }
                }
                items(items = uiState.musics, key = { it.id }) { music ->
                    MusicItem(
                        music = music,
                        playingMusicId = uiState.playingMusicId,
                        isMusicPlaying = uiState.isMusicPlaying,
                        playOrPause = playOrPause
                    )
                }
            }
        }
        if (!uiState.loading && uiState.musics.isEmpty()) EmptyScreen(
            R.string.no_musics, modifier = modifier.padding(
                ExtraLargeSpacing
            )
        )
    }
}

/**
 * Отображение заголовка экрана с музыкальными композициями.
 * @param sortOption Текущая опция сортировки музыкального списка.
 * @param onSortOption Функция для выбора опции сортировки.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun MusicScreenHeader(
    sortOption: String,
    onSortOption: (MusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние, определяющее, раскрыто ли меню с опциями сортировки.
    var expanded by remember { mutableStateOf(false) }
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.musics),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = ExtraLargeSpacing),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))

        // Отображение кнопки сортировки и выпадающего меню с опциями.
        Box {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.padding(end = ExtraLargeSpacing)
            ) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null
                )
            }

            // Отображение выпадающего меню с опциями сортировки.
            SortMusicsDropDownMenu(
                expanded = expanded,
                sortOption = sortOption,
                onDismissRequest = { expanded = false },
                onSortOption = {
                    onSortOption(it)
                    expanded = false
                }
            )
        }
    }
}

@Preview
@Composable
fun LoadedMusicsScreenPreview() {
    MaterialTheme {
        LoadedMusicsScreen(
            uiState = MusicsUiState(),
            play = {},
            shuffle = {},
            playOrPause = {},
            onSortOption = {}
        )
    }
}