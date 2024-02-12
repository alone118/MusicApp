package com.example.musicapp.presentation.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicapp.R
import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Music
import com.example.musicapp.presentation.components.FavouriteCard
import com.example.musicapp.presentation.components.PlaylistCard
import com.example.musicapp.presentation.screens.album.components.AlbumItem
import com.example.musicapp.presentation.screens.recently_played.components.RecentlyPlayedMusicItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Экран главной страницы, отображающий различные секции, такие как плейлисты, избранные треки,
 * топовые альбомы и недавно проигранные треки.
 *
 * @param navigateToPlaylistsScreen Функция навигации к экрану плейлистов.
 * @param navigateToAlbumDetail Функция навигации к деталям альбома.
 * @param navigateToFavouriteSongs Функция навигации к избранным трекам.
 * @param navigateToTopAlbums Функция навигации к топ альбомам.
 * @param navigateToRecentlyPlayed Функция навигации к недавно проигранным трекам.
 * @param modifier Модификатор, применяемый к корневому элементу.
 * @param viewModel ViewModel для управления данными на этом экране.
 */

@Composable
fun HomeScreen(
    navigateToPlaylistsScreen: () -> Unit,
    navigateToAlbumDetail: (String) -> Unit,
    navigateToFavouriteSongs: () -> Unit,
    navigateToTopAlbums: () -> Unit,
    navigateToRecentlyPlayed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Text(
            text = stringResource(id = R.string.home),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(ExtraLargeSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(ExtraLargeSpacing)
                ) {
                    PlaylistCard(
                        navigateToPlaylistsScreen = { navigateToPlaylistsScreen() },
                        modifier = Modifier.weight(1f)
                    )

                    FavouriteCard(
                        navigateToFavouriteSongs = { navigateToFavouriteSongs() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                AnimatedVisibility(
                    visible = uiState.topAlbums.isNotEmpty(),
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {
                    TopAlbumsRow(
                        albums = uiState.topAlbums,
                        navigateToAlbumDetail = { navigateToAlbumDetail },
                        navigateToTopAlbums = { navigateToTopAlbums() }
                    )
                }
            }
            recentlyPlayedScreenMusics(
                musics = uiState.recentlyPlayed,
                playOrPause = viewModel::playOrPause,
                navigateToRecentlyPlayed = { navigateToRecentlyPlayed() }
            )
        }
    }
}

/**
 * Отображение ряда с топовыми альбомами.
 *
 * @param albums Список альбомов для отображения.
 * @param navigateToAlbumDetail Функция навигации к деталям альбома.
 * @param navigateToTopAlbums Функция навигации к топ альбомам.
 * @param modifier Модификатор, применяемый к корневому элементу.
 */

@Composable
fun TopAlbumsRow(
    albums: List<Album>,
    navigateToAlbumDetail: () -> Unit,
    navigateToTopAlbums: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = albums.size - 1
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.top_albums),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.more),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = MediumSpacing)
                    .clickable(onClick = { navigateToTopAlbums() }),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        LazyRow(
            contentPadding = PaddingValues(
                start = ExtraLargeSpacing,
                top = MediumSpacing,
                end = ExtraLargeSpacing,
                bottom = ExtraLargeSpacing
            )
        ) {
            // Каждый элемент списка представлен компонентом AlbumItem, который отображает информацию об альбоме.
            itemsIndexed(items = albums, key = { _, album -> album.id }) { index, album ->
                AlbumItem(album = album, navigateToAlbumDetails = { navigateToAlbumDetail() })
                // Если альбом не является последним в списке, добавляется промежуток между элементами.
                if (index < lastIndex) Spacer(modifier = Modifier.width(ExtraLargeSpacing))
            }
        }
    }
}

/**
 * Отображение списка недавно проигранных треков.
 *
 * @param musics Список недавно проигранных треков для отображения.
 * @param playOrPause Функция для воспроизведения или паузы трека.
 * @param navigateToRecentlyPlayed Функция навигации к недавно проигранным трекам.
 * @param modifier Модификатор, применяемый к корневому элементу.
 */

fun LazyListScope.recentlyPlayedScreenMusics(
    musics: List<Music>,
    playOrPause: (String) -> Unit,
    navigateToRecentlyPlayed: () -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = ExtraLargeSpacing)
        ) {
            Text(
                text = stringResource(id = R.string.recently_played_musics),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.more),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(start = MediumSpacing)
                    .clickable(onClick = { navigateToRecentlyPlayed() }),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
    items(items = musics, key = { it.id }) { music ->
        RecentlyPlayedMusicItem(music = music, playOrPause = playOrPause)
    }
}