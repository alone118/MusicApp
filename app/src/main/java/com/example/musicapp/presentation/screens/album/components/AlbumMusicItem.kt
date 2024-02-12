package com.example.musicapp.presentation.screens.album.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Элемент списка музыки альбома.
 *
 * @param music Данные музыкального трека.
 * @param playOrPause Функция для воспроизведения или приостановки музыки.
 * @param modifier Модификатор компонента.
 */

@Composable
fun AlbumMusicItem(
    music: Music,
    playOrPause: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable { playOrPause(music.id.toString()) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = ExtraLargeSpacing)
        ) {
            if (music.trackNumber > 0) {
                // Проверяем, что номер трека больше 0, чтобы исключить ненумерованные треки.
                Text(
                    text = "${music.trackNumber}",
                    textAlign = TextAlign.End
                )
                SpacerWight(MediumSpacing)
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text(
                            text = music.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Divider(thickness = 0.5.dp)
                }
            }
        }
    }
}


@Preview
@Composable
fun AlbumMusicItemPreview() {
    MaterialTheme {
        AlbumMusicItem(
            music = Music(
                id = 1,
                uri = "",
                title = "Hello",
                artist = "Alan Walker",
                album = "Spectres",
                trackNumber = 2,
                artworkUri = "",
                artworkData = null
            ), playOrPause = {})
    }
}