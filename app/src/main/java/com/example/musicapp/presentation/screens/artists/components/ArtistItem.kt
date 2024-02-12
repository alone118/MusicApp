package com.example.musicapp.presentation.screens.artists.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicapp.data.models.Artist
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.ExtraLargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing

/**
 * Элемент списка артистов.
 *
 * @param artist Информация о артисте.
 * @param navigateToArtistDetails Функция для перехода к деталям артиста.
 * @param modifier Модификатор компонента.
 */


@Composable
fun ArtistItem(
    artist: Artist,
    navigateToArtistDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable { navigateToArtistDetails(artist.id) }) {
        Column(modifier = Modifier.padding(horizontal = ExtraLargeSpacing)) {
            SpacerHeight(MediumSpacing)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                SpacerWight(MediumSpacing)
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
            }
            SpacerHeight(MediumSpacing)
            Divider(thickness = 0.5.dp)
        }
    }
}


@Preview
@Composable
fun ArtistItemPreview() {
    MaterialTheme {
        ArtistItem(
            artist = Artist("", "", "Alan Walker"),
            navigateToArtistDetails = {}
        )
    }
}