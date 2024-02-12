package com.example.musicapp.presentation.screens.playlists.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.AudioWave
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Компонент для отображения элемента музыки в плейлисте.
 *
 * @param music Музыка, которая будет отображаться.
 * @param playingMusicId ID текущей играющей музыки.
 * @param isMusicPlaying Флаг, указывающий, играет ли музыка в данный момент.
 * @param playOrPause Функция обратного вызова для воспроизведения или приостановки музыки.
 * @param onRemoveMusic Функция обратного вызова для удаления музыки из плейлиста.
 * @param modifier Модификатор для настройки внешнего вида компонента.
 */

@Composable
fun PlaylistMusicsItem(
    music: Music,
    playingMusicId: String,
    isMusicPlaying: Boolean,
    playOrPause: (Music) -> Unit,
    onRemoveMusic: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    // Флаг, определяющий, раскрыто ли выпадающее меню
    var expended by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.clickable { playOrPause(music) }
    ) {
        SpacerHeight(MediumSpacing)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(start = ExtraLargeSpacing)
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
                // Отображение анимации звуковых волн, если музыка играет
                if (music.id.toString() == playingMusicId) {
                    AudioWave(isMusicPlaying = isMusicPlaying)
                }
            }
            SpacerWight(MediumSpacing)
            Column(modifier = Modifier.padding(end = ExtraLargeSpacing).weight(1f)) {
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
            Box(modifier = Modifier.padding(horizontal = MediumSpacing)) {
                IconButton(onClick = { expended = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null
                    )
                }
                // Выпадающее меню для удаления музыки из плейлиста
                PlaylistMusicDropDownMenu(
                    expended = expended,
                    onDismissRequest = { expended = false },
                    onRemoveMusic = {
                        onRemoveMusic(music.id)
                        expended = false
                    }
                )
            }
        }
        SpacerHeight(MediumSpacing)
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(horizontal = ExtraLargeSpacing))
    }
}