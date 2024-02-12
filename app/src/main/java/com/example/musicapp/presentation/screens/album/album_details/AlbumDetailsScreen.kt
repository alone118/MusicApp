package com.example.musicapp.presentation.screens.album.album_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIos
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.components.MediaControls
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.screens.album.components.AlbumMusicItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.theme.SmallSpacing

/**
 * Экран деталей альбома.
 *
 * @param onBackPress Функция обратного вызова для нажатия кнопки "Назад".
 * @param modifier Модификатор, применяемый к экрану.
 * @param viewModel ViewModel для управления данными экрана.
 */

@Composable
fun AlbumDetailsScreen(
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlbumDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    LoadedAlbumDetailsScreen(
        uiState = uiState,
        playOrPause = viewModel::playOrPause,
        play = viewModel::play,
        shuffle = viewModel::shuffle,
        onBackPress = { onBackPress() },
        modifier = modifier
    )
}

/**
 * Экран деталей альбома с загруженными данными.
 *
 * @param uiState Состояние UI с данными альбома.
 * @param playOrPause Функция для воспроизведения или паузы музыки.
 * @param play Функция для начала воспроизведения музыки.
 * @param shuffle Функция для перемешивания списка воспроизведения.
 * @param onBackPress Функция обратного вызова для нажатия кнопки "Назад".
 * @param modifier Модификатор, применяемый к экрану.
 */

@Composable
fun LoadedAlbumDetailsScreen(
    uiState: AlbumDetailsUiState,
    playOrPause: (String) -> Unit,
    play: () -> Unit,
    shuffle: () -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBackPress() }) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = Icons.Rounded.ArrowBackIos,
                    contentDescription = null
                )
            }
            Text(
                text = uiState.album.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = MediumSpacing)
        ) {
            item {
                (AlbumInfo(
                    uiState = uiState,
                    modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
                ))
            }
            item { SpacerHeight(LargeSpacing) }
            if (!uiState.loading && uiState.musics.isNotEmpty()) {
                item {
                    MediaControls(
                        play = { play() },
                        shuffle = { shuffle() },
                        modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
                    )
                }
                item { SpacerHeight(LargeSpacing) }
                albumMusicsCollection(uiState = uiState, playOrPause = playOrPause)
            } else {
                item {
                    EmptyScreen(
                        textResourceId = R.string.no_musics,
                        modifier = Modifier.padding(ExtraLargeSpacing)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoadedAlbumDetailsScreenPreview() {
    LoadedAlbumDetailsScreen(
        uiState = AlbumDetailsUiState(),
        playOrPause = {},
        play = {},
        shuffle = {},
        onBackPress = {})
}

/**
 * Компонент с информацией об альбоме.
 *
 * @param uiState Состояние UI с данными альбома.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun AlbumInfo(
    uiState: AlbumDetailsUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.album.artworkUri)
                    .crossfade(true)
                    .build(),
                error = painterResource(id = R.drawable.music_album_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(172.dp)
                    .clip(MaterialTheme.shapes.small),
                contentScale = ContentScale.Crop
            )
            SpacerWight(MediumSpacing)
            Column {
                Text(
                    text = uiState.album.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uiState.album.artist,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (uiState.album.year > 0) {
                    Text(
                        text = "Album • ${uiState.album.year}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        SpacerHeight(MediumSpacing)
        Divider(thickness = 0.5.dp)
    }
}

@Preview
@Composable
fun AlbumInfoPreview() {
    AlbumInfo(
        uiState = AlbumDetailsUiState()
    )
}

/**
 * Функция для отображения коллекции музыки альбома.
 *
 * @param uiState Состояние UI с данными альбома.
 * @param playOrPause Функция для воспроизведения или паузы музыки.
 */

fun LazyListScope.albumMusicsCollection(
    uiState: AlbumDetailsUiState,
    playOrPause: (String) -> Unit
) {
    val stringValue = if (uiState.musics.size > 1) R.string.musics else R.string.music
    item {
        Text(
            text = "${uiState.musics.size} $stringValue",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
    }
    item { Spacer(modifier = Modifier.height(SmallSpacing)) }
    item {
        Divider(
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
    }
    items(items = uiState.musics) { music ->
        AlbumMusicItem(music = music, playOrPause = playOrPause)
    }
}