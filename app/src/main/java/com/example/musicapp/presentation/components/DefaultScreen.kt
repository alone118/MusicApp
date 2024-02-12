package com.example.musicapp.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musicapp.presentation.theme.ExtraLargeSpacing

/**
 * Компонент для отображения стандартного экрана.
 *
 * @param error Текст сообщения об ошибке, отображаемый на экране. Пустая строка означает отсутствие ошибки.
 * @param loading Флаг, указывающий на загрузку данных. Если `true`, отображается индикатор загрузки.
 * @param modifier Модификатор, применяемый к компоненту.
 * @param content Функция, определяющая содержимое экрана. Включает в себя разметку, отображаемую над сообщением об ошибке и индикатором загрузки.
 */

@Composable
fun DefaultScreen(
    error: String,
    loading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Отображение пользовательского содержимого экрана.
        content()

        // Отображение сообщения об ошибке, если оно не пустое.
        if (error.isNotBlank()) {
            Text(text = error, modifier = Modifier.padding(ExtraLargeSpacing))
        }

        // Отображение индикатора загрузки, если установлен флаг loading.
        if (loading) {
            CircularProgressIndicator()
        }
    }
}