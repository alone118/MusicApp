package com.example.musicapp.presentation.screens.recently_played

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.MediaControls
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.screens.recently_played.components.MusicItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing

/**
 * Отображение экрана недавно проигранных музыкальных композиций.
 *
 * @param onBack Функция обратного вызова для возврата на предыдущий экран
 * @param modifier Модификатор для управления внешним видом экрана
 * @param viewModel ViewModel для управления состоянием экрана
 */

@Composable
fun RecentlyPlayedMusicScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecentlyPlayedViewModel
) {

    // Получение состояния UI из ViewModel
    val uiState by viewModel.uiState.collectAsState()

    DefaultScreen(
        error = uiState.error,
        loading = uiState.loading,
        modifier = modifier
    ) {
        RecentlyPlayedMusics(
            musics = uiState.recentlyPlayed,
            play = viewModel::play,
            shuffle = viewModel::shuffle,
            playOrPause = viewModel::playOrPause,
            onBack = { onBack() })
    }
}

/**
 * Отображение списка недавно проигранных музыкальных композиций.
 *
 * @param musics Список недавно проигранных музыкальных композиций
 * @param play Функция для начала воспроизведения музыки
 * @param shuffle Функция для случайного воспроизведения музыки
 * @param playOrPause Функция для воспроизведения или приостановки музыки
 * @param onBack Функция обратного вызова для возврата на предыдущий экран
 * @param modifier Модификатор для управления внешним видом списка
 */

@Composable
fun RecentlyPlayedMusics(
    musics: List<Music>,
    play: () -> Unit,
    shuffle: () -> Unit,
    playOrPause: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Отображение списка музыки с кнопками управления воспроизведением и заголовком "Недавно проигранные"
    Column(modifier = modifier) {
        RecentlyPlayedHeader(onBack = { onBack() })
        MediaControls(
            play = { play() },
            shuffle = { shuffle() },
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item { SpacerHeight(ExtraLargeSpacing) }
            items(items = musics, key = { it.id }) { music ->
                MusicItem(music = music, playOrPause = playOrPause)
            }
        }
    }
}

@Preview
@Composable
fun RecentlyPlayedMusicsPreview() {
    MaterialTheme {
        RecentlyPlayedMusics(
            musics = listOf(Music(), Music()),
            play = {},
            shuffle = {},
            playOrPause = {},
            onBack = {}
        )
    }
}

/**
 * Отображение заголовка экрана недавно проигранных музыкальных композиций.
 *
 * @param onBack Функция обратного вызова для возврата на предыдущий экран
 * @param modifier Модификатор для управления внешним видом заголовка
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyPlayedHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Отображение верхней панели приложения с заголовком "Недавно проигранные" и кнопкой "назад"
    TopAppBar(title = {
        Text(
            text = stringResource(id = R.string.recently_played),
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