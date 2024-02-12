package com.example.musicapp.presentation.screens.playlists.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.R
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.theme.ExtraLargeSpacing

/**
 * Компонент для выбора из списка музыкальных треков.
 *
 * @param musics Список музыкальных треков.
 * @param selectedMusics Множество идентификаторов выбранных треков.
 * @param onSelect Функция обратного вызова для выбора трека.
 * @param onSave Функция обратного вызова для сохранения выбранных треков.
 * @param onCancel Функция обратного вызова для отмены выбора треков.
 * @param modifier Модификатор компонента.
 */

@Composable
fun SelectableMusics(
    musics: List<Music>,
    selectedMusics: Set<Long>,
    onSelect: (Long) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ExtraLargeSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { onSave() }) {
                Text(
                    text = stringResource(id = R.string.done),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Box(contentAlignment = Alignment.Center) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier = Modifier.height(ExtraLargeSpacing)) }
                items(items = musics) { music ->
                    SelectableMusicItem(
                        music = music,
                        selected = selectedMusics.contains(music.id),
                        onSelect = onSelect
                    )
                }
            }
            // Отображение экрана с информацией о пустом списке, если список музыкальных треков пуст
            if (musics.isEmpty()) EmptyScreen(
                R.string.no_musics, modifier = modifier.padding(
                    ExtraLargeSpacing
                )
            )
        }
    }
}


@Preview("SelectableMusics")
@Preview("SelectableMusics (dark)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectableMusicsOffPreview() {
    SelectableMusicsPreview()
}

@Composable
fun SelectableMusicsPreview() {
    MaterialTheme {
        Surface {
            SelectableMusics(
                musics = listOf(
                    Music(id = 10, title = "Believer", album = "Dragons", artist = "Imagine Dragons"),
                    Music(id = 11, title = "Unstoppable", album = "SIA", artist = "Sia"),
                    Music(id = 12, title = "Heat Waves", album = "Heat Heat", artist = "Glass Animals"),
                    Music(id = 13, title = "Faded", album = "Fade", artist = "Alan Walker"),
                    Music(id = 14, title = "Summertime", album = "Summer", artist = "K-391"),
                ),
                selectedMusics = setOf(10,12,13),
                onSelect = {},
                onSave = {},
                onCancel = {}
            )
        }
    }
}