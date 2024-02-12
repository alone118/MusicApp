package com.example.musicapp.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

/**
 * Компонент для отображения пустого экрана с текстом.
 *
 * @param textResourceId Ресурс идентификатора строки, которая будет отображаться на пустом экране.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun EmptyScreen(
    textResourceId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = textResourceId),
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.SemiBold
    )
}