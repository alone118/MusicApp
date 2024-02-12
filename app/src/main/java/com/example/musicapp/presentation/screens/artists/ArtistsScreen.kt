package com.example.musicapp.presentation.screens.artists

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.R
import com.example.musicapp.presentation.components.DefaultScreen
import com.example.musicapp.presentation.components.EmptyScreen
import com.example.musicapp.presentation.screens.artists.components.ArtistItem
import com.example.musicapp.presentation.theme.ExtraLargeSpacing

/**
 * Экран отображения списка артиста.
 *
 * @param artistsId Идентификатор артиста.
 * @param navigateToArtistDetails Функция для перехода к деталям артиста.
 */

@Composable
fun ArtistsScreen(
    artistsId: String,
    navigateToArtistDetails: (String) -> Unit,
) {

    val viewModel: ArtistsViewModel =
        viewModel(factory = ArtistsViewModel.provideFactory(artistsId))

    val uiState by viewModel.uiState.collectAsState()
    LoadedArtistsScreen(
        uiState = uiState,
        navigateToArtistDetails = navigateToArtistDetails
    )
}

/**
 * Загруженный экран отображения списка артиста.
 *
 * @param uiState Состояние пользовательского интерфейса для отображения списка артистов.
 * @param navigateToArtistDetails Функция для перехода к деталям артиста.
 * @param modifier Модификатор для настройки внешнего вида.
 */

@Composable
fun LoadedArtistsScreen(
    uiState: ArtistsUiState,
    navigateToArtistDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScreen(error = uiState.error, loading = uiState.loading) {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            Text(
                text = stringResource(id = R.string.artists),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = ExtraLargeSpacing)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = uiState.artists, key = { it.id }) { artist ->
                    ArtistItem(
                        artist = artist,
                        navigateToArtistDetails = navigateToArtistDetails
                    )
                }
            }
        }
        if (!uiState.loading && uiState.artists.isEmpty()) EmptyScreen(
            textResourceId = R.string.no_artists_found,
            modifier = Modifier.padding(ExtraLargeSpacing)
        )
    }
}


@Preview
@Composable
fun LoadedArtistsScreenPreview() {
    MaterialTheme {
        LoadedArtistsScreen(
            uiState = ArtistsUiState(),
            navigateToArtistDetails = {},
        )
    }
}