package com.example.musicapp.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Компонент для отображения аудиоволны.
 *
 * @param isMusicPlaying Флаг, указывающий, играет ли музыка в данный момент.
 * @param modifier Модификатор, применяемый к компоненту.
 */

@Composable
fun AudioWave(
    isMusicPlaying: Boolean,
    modifier: Modifier = Modifier
) {

    // Создание трех анимаций для аудиоволн.
    val transition1 = rememberInfiniteTransition()
    val transition2 = rememberInfiniteTransition()
    val transition3 = rememberInfiniteTransition()

    // Анимация для первой аудиоволны.
    val fraction1 by transition1.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                durationMillis = 1600,
                easing = LinearEasing
            )
        ), label = ""
    )

    // Анимация для второй аудиоволны.
    val fraction2 by transition2.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                durationMillis = 800,
                easing = LinearEasing
            )
        ), label = ""
    )

    // Анимация для третьей аудиоволны.
    val fraction3 by transition3.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            )
        ), label = ""
    )

    // Отображение трех аудиоволн в виде горизонтальной строки.
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.size(24.dp)
    ) {

        // Первая аудиоволна.
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(if (!isMusicPlaying) 0.3f else fraction1)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary)
        )

        // Вторая аудиоволна.
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(if (!isMusicPlaying) 0.5f else fraction2)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary)
        )

        // Третья аудиоволна.
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(if (!isMusicPlaying) 0.3f else fraction3)
                .clip(RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}