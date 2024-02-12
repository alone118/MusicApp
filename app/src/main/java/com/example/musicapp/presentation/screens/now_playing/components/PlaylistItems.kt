package com.example.musicapp.presentation.screens.now_playing.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.AudioWave
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.screens.musics.MusicsUiState
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Отображает элементы плейлиста.
 * @param uiState Состояние UI для отображения музыкальных треков.
 * @param musics Список музыкальных композиций в плейлисте.
 * @param nowPlaying Текущая воспроизводимая музыка.
 * @param play Функция для начала воспроизведения музыки.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun PlaylistItems(
    uiState: MusicsUiState,
    musics: List<Music>,
    nowPlaying: Music,
    play: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (musics.isNotEmpty()) {
        // Разделяем музыкальные композиции на предыдущие и последующие по отношению к текущей воспроизводимой.
        val previousMusics = musics.subList(0, musics.indexOf(nowPlaying))
        val nextMusics = musics.subList(musics.indexOf(nowPlaying) + 1, musics.size)
        val lazyListState = rememberLazyListState()
        // Прокручиваем список к текущей воспроизводимой музыке с анимацией.
        LaunchedEffect(lazyListState) {
            lazyListState.animateScrollToItem(musics.indexOf(nowPlaying))
        }
        LazyColumn(
            modifier = modifier
                .size(500.dp),
            contentPadding = PaddingValues(horizontal = ExtraLargeSpacing),
            state = lazyListState
        ) {
            // Отображаем предыдущие музыкальные композиции.
            items(items = previousMusics, key = { it.id }) { music ->
                MusicItem(
                    music = music,
                    play = play,
                    playingMusicId = uiState.playingMusicId,
                    isMusicPlaying = uiState.isMusicPlaying
                )
            }
            // Отображаем текущую воспроизводимую музыку.
            item(key = nowPlaying.id) {
                Column {
                    SpacerHeight(LargeSpacing)
                    MusicItem(
                        music = nowPlaying,
                        play = play,
                        playingMusicId = uiState.playingMusicId,
                        isMusicPlaying = uiState.isMusicPlaying
                    )
                    SpacerHeight(LargeSpacing)
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.playing_next),
                    fontWeight = FontWeight.Bold
                )
            }

            // Отображаем последующие музыкальные композиции.
            items(items = nextMusics, key = { it.id }) { music ->
                MusicItem(
                    music = music,
                    play = play,
                    playingMusicId = uiState.playingMusicId,
                    isMusicPlaying = uiState.isMusicPlaying
                )
            }
        }
    }
}

/**
 * Отображает элемент музыкальной композиции в списке плейлиста.
 * @param music Музыкальная композиция для отображения.
 * @param playingMusicId ID воспроизводимой музыки.
 * @param isMusicPlaying Флаг, указывающий, воспроизводится ли музыка в данный момент.
 * @param play Функция для начала воспроизведения музыки.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun MusicItem(
    music: Music,
    playingMusicId: String,
    isMusicPlaying: Boolean,
    play: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.clickable { play(music.id.toString()) }
    ) {
        SpacerHeight(MediumSpacing)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(music.artworkData)
                    .crossfade(true)
                    .build(),
                error = painterResource(id = R.drawable.music_note),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            // Если музыка воспроизводится, отображаем анимацию волн аудио.
            if (music.id.toString() == playingMusicId) {
                AudioWave(isMusicPlaying = isMusicPlaying)
            }
            SpacerWight(MediumSpacing)
            Column {
                Text(
                    text = music.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = music.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        SpacerHeight(MediumSpacing)
        Divider(thickness = 0.5.dp)
    }
}


@Preview
@Composable
fun PlaylistItemsPreview() {
    MaterialTheme {
        PlaylistItems(
            musics = listOf(Music()),
            nowPlaying = Music(),
            play = {},
            uiState = MusicsUiState()
        )
    }
}