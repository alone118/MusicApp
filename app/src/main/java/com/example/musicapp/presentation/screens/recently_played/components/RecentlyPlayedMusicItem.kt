package com.example.musicapp.presentation.screens.recently_played.components

import androidx.compose.foundation.clickable
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
import com.example.musicapp.presentation.theme.SmallSpacing

/**
 * Компонент элемента недавно воспроизведенной музыки.
 *
 * @param music Данные о музыкальном треке.
 * @param playOrPause Функция для воспроизведения или приостановки музыкального трека.
 * @param modifier Модификатор для настройки внешнего вида компонента.
 */

@Composable
fun RecentlyPlayedMusicItem(
    music: Music,
    playOrPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = SmallSpacing)
            .clickable { playOrPause(music.id.toString()) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
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
                    text = "${music.album} - ${music.artist}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        SpacerHeight(MediumSpacing)
        Divider(
            modifier = Modifier
                .padding(start = 75.dp, end = 15.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview
@Composable
fun RecentlyPlayedMusicItemPreview() {
    MaterialTheme {
        RecentlyPlayedMusicItem(
            music = Music(),
            playOrPause = {}
        )
    }
}