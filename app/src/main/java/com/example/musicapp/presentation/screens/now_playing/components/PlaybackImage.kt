package com.example.musicapp.presentation.screens.now_playing.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOneOn
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.ShuffleOn
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.data.cache.datastore.RepeatMode
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.formatDuration
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Отображает обложку музыкальной компазиции.
 * @param music Музыка, для которой отображается обложка.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun PlaybackImage(
    music: Music,
    modifier: Modifier = Modifier
) {
    // Загружаем асинхронное изображение с использованием библиотеки Coil.
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(music.artworkData)
            .crossfade(true)
            .build(),
        error = painterResource(id = R.drawable.music_note),
        contentDescription = null,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop
    )
}

@Preview
@Composable
fun PlaybackImagePreview() {
    MaterialTheme {
        PlaybackImage(music = Music())
    }
}

/**
 * Отображает метаданные воспроизведения музыки.
 * @param music Музыка, для которой отображаются метаданные.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaybackMetaData(
    music: Music,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = MediumSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = music.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
        SpacerHeight(MediumSpacing)
        Text(
            text = music.artist,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()
        )
    }
}


@Preview
@Composable
fun PlaybackMetaDataPreview() {
    MaterialTheme {
        PlaybackMetaData(
            music = Music(),
        )
    }
}

/**
 * Отображает панель управления воспроизведением.
 * @param shuffleModeEnabled Флаг, указывающий, включен ли режим случайного воспроизведения.
 * @param repeatMode Режим повтора воспроизведения.
 * @param isFavourite Флаг, указывающий, добавлена ли музыка в избранное.
 * @param toggleShuffleMode Функция для включения/выключения режима случайного воспроизведения.
 * @param updateRepeatMode Функция для обновления режима повтора воспроизведения.
 * @param toggleFavourite Функция для добавления/удаления музыки из избранного.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun PlaybackOptions(
    shuffleModeEnabled: Boolean,
    repeatMode: RepeatMode,
    isFavourite: Boolean,
    toggleShuffleMode: () -> Unit,
    updateRepeatMode: () -> Unit,
    toggleFavourite: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Определяем иконки для различных режимов воспроизведения и состояний.
    val repeatIcon = when (repeatMode) {
        RepeatMode.ONE -> Icons.Rounded.RepeatOneOn
        RepeatMode.ALL -> Icons.Rounded.RepeatOn
        RepeatMode.OFF -> Icons.Rounded.Repeat
    }
    val shuffleIcon = if (shuffleModeEnabled) Icons.Rounded.ShuffleOn else Icons.Rounded.Shuffle
    val favouriteIcon = if (isFavourite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder
    val favouriteIconTint = if (isFavourite) Color.Red else LocalContentColor.current

    val iconModifier = Modifier.size(48.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { toggleShuffleMode() },
            modifier = iconModifier
        ) {
            Icon(
                imageVector = shuffleIcon,
                contentDescription = null
            )
        }
        IconButton(
            onClick = {},
            modifier = iconModifier
        ) {
            Icon(
                imageVector = Icons.Rounded.PlaylistAdd,
                contentDescription = null
            )
        }
        IconButton(
            onClick = { toggleFavourite() },
            modifier = iconModifier
        ) {
            Icon(
                imageVector = favouriteIcon,
                contentDescription = null,
                tint = favouriteIconTint
            )
        }
        IconButton(
            onClick = { updateRepeatMode() },
            modifier = iconModifier
        ) {
            Icon(
                imageVector = repeatIcon,
                contentDescription = null,
            )
        }
    }
}


@Preview
@Composable
fun PlaybackOptionsPreview() {
    MaterialTheme {
        PlaybackOptions(
            shuffleModeEnabled = true,
            repeatMode = RepeatMode.ALL,
            isFavourite = false,
            toggleShuffleMode = {},
            updateRepeatMode = {},
            toggleFavourite = {}
        )
    }
}

/**
 * Отображает элементы управления воспроизведением, такие как кнопки воспроизведения/паузы, переключения треков и т. д.
 * @param isPlaying Флаг, указывающий, воспроизводится ли музыка в данный момент.
 * @param duration Продолжительность музыки.
 * @param position Текущая позиция воспроизведения музыки.
 * @param showPlaylistItems Флаг, указывающий, отображается ли список плейлиста.
 * @param onPositionChange Функция для обновления текущей позиции воспроизведения.
 * @param playNextMusic Функция для воспроизведения следующей музыки.
 * @param playPreviousMusic Функция для воспроизведения предыдущей музыки.
 * @param playOrPause Функция для воспроизведения или приостановки музыки.
 * @param togglePlaylistItems Функция для отображения/скрытия списка плейлиста.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    duration: Long,
    position: Long,
    showPlaylistItems: Boolean,
    onPositionChange: (Float) -> Unit,
    playNextMusic: () -> Unit,
    playPreviousMusic: () -> Unit,
    playOrPause: () -> Unit,
    togglePlaylistItems: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние для слайдера воспроизведения.
    var sliderPosition by remember { mutableStateOf<Float?>(null) }

    val iconModifier = Modifier.size(48.dp)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Slider(
            value = sliderPosition ?: position.toFloat(),
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                sliderPosition?.let {
                    onPositionChange(it)
                    sliderPosition = null
                }
            },
            valueRange = 0f..duration.toFloat(),
            modifier = Modifier.padding(horizontal = MediumSpacing)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MediumSpacing)
        ) {
            Text(
                text = formatDuration(sliderPosition?.toLong() ?: position),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = formatDuration(duration),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
        SpacerHeight(size = MediumSpacing)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { playPreviousMusic() }) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
            IconButton(onClick = { playOrPause() }) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = null,
                        modifier = iconModifier
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        modifier = iconModifier
                    )
                }
            }
            IconButton(onClick = { playNextMusic() }) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = null,
                    modifier = iconModifier
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { togglePlaylistItems() },
                modifier = iconModifier.semantics(true) {
                    contentDescription = ""
                }
            ) {

                // Отображаем иконку списка плейлиста или его скрытия в зависимости от текущего состояния.
                if (showPlaylistItems) {
                    Icon(
                        painter = painterResource(id = R.drawable.list_box),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.list_box_outline),
                        contentDescription = null
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PlaybackControlsPreview() {
    MaterialTheme {
        PlaybackControls(
            isPlaying = false,
            duration = 59,
            position = 1,
            showPlaylistItems = false,
            onPositionChange = {},
            playNextMusic = {},
            playPreviousMusic = {},
            playOrPause = {},
            togglePlaylistItems = {}
        )
    }
}