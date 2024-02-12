package com.example.musicapp.presentation.screens.playlists.playlistMusics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.cache.datastore.PlaylistMusicsSortOption
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.components.MediaControls
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.screens.playlists.components.PlaylistMusicsItem
import com.example.musicapp.presentation.screens.playlists.components.PlaylistMusicsSortDropDownMenu
import com.example.musicapp.presentation.screens.playlists.components.SelectableMusics
import com.example.musicapp.presentation.theme.ExtraLargeSpacing

/**
 * Отображение экрана списка музыки в плейлисте.
 *
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param viewModel ViewModel для управления состоянием экрана
 * @param modifier Модификатор для управления внешним видом экрана
 */

@Composable
fun PlaylistMusicsScreen(
    onBackPress: () -> Unit,
    viewModel: PlaylistMusicsViewModel,
    modifier: Modifier = Modifier
) {
    // Получение состояния UI из ViewModel
    val uiState by viewModel.uiState.collectAsState()
    // Состояние для отображения выбора музыки
    var showSelectableMusics by remember { mutableStateOf(false) }

    // Отображение загруженного экрана списка музыки
    LoadedPlaylistMusicsScreen(
        uiState = uiState,
        play =viewModel::play,
        shuffle =viewModel::shuffle,
        playOrPause = { viewModel.playOrPause(it.id.toString()) },
        onRemoveMusic =viewModel::removeMusic,
        onBackPress = { onBackPress() },
        onShowSelectableMusics = { showSelectableMusics = true },
        onSortOption = viewModel::setMusicsSortOption
    )

    // Анимированное отображение списка выбираемой музыки
    AnimatedVisibility(
        visible = showSelectableMusics,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        SelectableMusics(
            musics = uiState.allMusics,
            selectedMusics = uiState.selectedMusics,
            onSelect = viewModel::updateSelectedMusics,
            onSave = {
                     viewModel.insertMusics()
                viewModel.updateSelectedMusics(0,true)
                showSelectableMusics = false
            },
            onCancel = {
                viewModel.updateSelectedMusics(0,true)
                showSelectableMusics = false
            },
            modifier = modifier.statusBarsPadding()
        )
    }
}

/**
 * Отображение экрана списка музыки в плейлисте после загрузки данных.
 *
 * @param uiState Состояние UI экрана списка музыки
 * @param play Функция для начала воспроизведения музыки
 * @param shuffle Функция для перемешивания списка музыки
 * @param playOrPause Функция для воспроизведения или приостановки музыки
 * @param onRemoveMusic Функция для удаления музыки из плейлиста
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param onShowSelectableMusics Функция для отображения выбора музыки
 * @param onSortOption Функция для установки опции сортировки музыки
 * @param modifier Модификатор для управления внешним видом экрана
 */

@Composable
fun LoadedPlaylistMusicsScreen(
    uiState: PlaylistMusicsUiState,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (Music) -> Unit,
    onRemoveMusic: (Long) -> Unit,
    onBackPress: () -> Unit,
    onShowSelectableMusics: () -> Unit,
    onSortOption: (PlaylistMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {

    // Отображение содержимого экрана списка музыки
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Заголовок экрана списка музыки
        PlaylistMusicsScreenHeader(
            playlistName = uiState.playlist.name,
            sortOption = uiState.sortOption.name,
            onBackPress = { onBackPress() },
            onShowSelectableMusics = { onShowSelectableMusics() },
            onSortMusics = onSortOption
        )
        SpacerHeight(ExtraLargeSpacing)
        // Контроль медиа-плеера (воспроизведение, перемешивание)
        MediaControls(
            play = { play() },
            shuffle = { shuffle() },
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
        // Список музыки в плейлисте
        Box(contentAlignment = Alignment.Center) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier = Modifier.height(ExtraLargeSpacing)) }
                items(items = uiState.playlistMusics, key = { it.id }) { music ->
                    PlaylistMusicsItem(
                        music = music,
                        playingMusicId = uiState.playingMusicId,
                        isMusicPlaying = uiState.isMusicPlaying,
                        playOrPause = playOrPause,
                        onRemoveMusic = onRemoveMusic
                    )
                }
            }
            // Пустой экран при отсутствии музыки в плейлисте
            if (!uiState.loading && uiState.playlistMusics.isEmpty()) EmptyScreen(
                R.string.no_playlists_musics, modifier = modifier.padding(
                    ExtraLargeSpacing
                )
            )
        }
    }
}

@Preview
@Composable
fun LoadedPlaylistMusicsScreenPreview(){
    MaterialTheme{
        LoadedPlaylistMusicsScreen(
            uiState = PlaylistMusicsUiState(),
            play = { },
            shuffle = { },
            playOrPause = {},
            onRemoveMusic = {},
            onBackPress = { },
            onShowSelectableMusics = { },
            onSortOption = {}
        )
    }
}

/**
 * Отображение заголовка экрана списка музыки в плейлисте.
 *
 * @param playlistName Название плейлиста
 * @param sortOption Опция сортировки музыки в плейлисте
 * @param onBackPress Функция обратного вызова для возврата на предыдущий экран
 * @param onShowSelectableMusics Функция для отображения выбора музыки
 * @param onSortMusics Функция для установки опции сортировки музыки
 * @param modifier Модификатор для управления внешним видом компонента
 */

@Composable
fun PlaylistMusicsScreenHeader(
    playlistName: String,
    sortOption: String,
    onBackPress: () -> Unit,
    onShowSelectableMusics: () -> Unit,
    onSortMusics: (PlaylistMusicsSortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние для отображения выпадающего меню сортировки
    var expended by remember { mutableStateOf(false) }

    // Отображение заголовка
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
        // Название плейлиста
        Text(
            text = playlistName,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        // Кнопка для отображения меню сортировки
        Box {
            IconButton(onClick = { expended = true }) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Rounded.Sort,
                    contentDescription = null
                )
            }
            // Меню сортировки музыки
            PlaylistMusicsSortDropDownMenu(
                expanded = expended,
                sortOption = sortOption,
                onDismissRequest = { expended = false },
                onSortOption = {
                    onSortMusics(it)
                    expended = false
                }
            )
        }
        // Кнопка для отображения выбора музыки
        IconButton(onClick = { onShowSelectableMusics() }) {
            Icon(
                modifier = Modifier.size(34.dp),
                imageVector = Icons.Rounded.Add,
                contentDescription = null
            )
        }
    }
}