package com.example.musicapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Компонент для отображения элемента музыки в списке.
 *
 * @param music Объект музыки для отображения.
 * @param playingMusicId Идентификатор текущей воспроизводимой музыки.
 * @param isMusicPlaying Флаг, указывающий, играет ли музыка в данный момент.
 * @param playOrPause Функция обратного вызова для запуска или приостановки воспроизведения музыки.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun MusicItem(
    music: Music,
    playingMusicId: String,
    isMusicPlaying: Boolean,
    playOrPause: (Music) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.clickable { playOrPause(music) }
    ) {
        SpacerHeight(MediumSpacing)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Блок с изображением обложки альбома музыки.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(start = ExtraLargeSpacing)
            ) {
                AsyncImage(
                    // Загрузка изображения альбома с помощью библиотеки Coil.
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(music.artworkData)
                        .crossfade(true)
                        .build(),
                    // Иконка, отображаемая в случае ошибки загрузки изображения.
                    error = painterResource(id = R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                // Отображение аудиоволны, если текущая музыка совпадает с музыкой, воспроизводимой в данный момент.
                if (music.id.toString() == playingMusicId) {
                    AudioWave(isMusicPlaying = isMusicPlaying)
                }
            }
            SpacerWight(MediumSpacing)
            // Колонка с названием и исполнителем музыки.
            Column(modifier = Modifier.padding(end = ExtraLargeSpacing)) {
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
        // Горизонтальная линия-разделитель между элементами списка.
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = ExtraLargeSpacing))
    }
}


@Preview
@Composable
fun MusicItemPreview() {
    MusicItem(
        music = Music(title = "Faded", artist = "Alan Walker"),
        playingMusicId = "",
        isMusicPlaying = true,
        playOrPause = {}
    )
}