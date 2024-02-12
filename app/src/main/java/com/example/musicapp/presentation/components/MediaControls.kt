package com.example.musicapp.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp.R
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.theme.LargeSpacing

/**
 * Компонент для отображения элементов управления медиа-плеером.
 *
 * @param play Функция обратного вызова для воспроизведения медиа-контента.
 * @param shuffle Функция обратного вызова для перемешивания списка воспроизведения.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun MediaControls(
    play: () -> Unit,
    shuffle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Button(
            onClick = { play() },
            modifier = Modifier.weight(1f),
            shape = CircleShape
        ) {
            Text(
                text = stringResource(id = R.string.play),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
        SpacerWight(LargeSpacing)
        Button(
            onClick = { shuffle() },
            modifier = Modifier.weight(1f),
            shape = CircleShape
        ) {
            Text(
                text = stringResource(id = R.string.shuffle),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Preview
@Composable
fun MediaControlsPreview() {
    MediaControls(play = {}, shuffle = {})
}