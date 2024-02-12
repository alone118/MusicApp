package com.example.musicapp.presentation.screens.playlists.components

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
import com.example.musicapp.presentation.theme.SmallElevation
import com.example.musicapp.presentation.theme.SmallSpacing

/**
 * Компонент элемента списка выбираемой музыки.
 *
 * @param music Объект музыки.
 * @param selected Флаг, указывающий, выбран ли элемент.
 * @param onSelect Функция обратного вызова для выбора элемента.
 * @param modifier Модификатор для настройки внешнего вида компонента.
 */

@Composable
fun SelectableMusicItem(
    music: Music,
    selected: Boolean,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onSelect(music.id) }
    ) {
        SpacerHeight(MediumSpacing)
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = music.title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                SpacerHeight(SmallElevation)
                Text(
                    text = music.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            SpacerWight(MediumSpacing)
            SelectMusicButton(selected = selected)
        }
        SpacerHeight(MediumSpacing)
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(start = 88.dp, end = 24.dp))
    }
}



@Preview
@Composable
fun SelectableMusicItemPreview() {
    MaterialTheme {
        SelectableMusicItem(music = Music(title = "Faded", artist = "Alan Walker"), selected = false, onSelect = {})
    }
}