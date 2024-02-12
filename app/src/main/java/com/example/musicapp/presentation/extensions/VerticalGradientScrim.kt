package com.example.musicapp.presentation.extensions

import androidx.annotation.FloatRange
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Modifier для вертикального градиентного затемнения.
 *
 * @param color Цвет градиента.
 * @param startYPercentage Процент начальной точки градиента по вертикали.
 * @param endYPercentage Процент конечной точки градиента по вертикали.
 * @param decay Коэффициент затухания прозрачности градиента.
 * @param numStops Количество точек в градиенте.
 */

fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16
): Modifier = composed {

    // Создаем список цветов для градиента с учетом затухания.
    val colors = remember(color, numStops){
        if (decay != 1f){
            val baseAlfa = color.alpha
            List(numStops){i ->
                val x = i * 1f / (numStops -1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlfa * opacity)
            }
            // Если затухание не установлено, используем два цвета: полностью прозрачный и заданный цвет.
        }else {
            listOf(color.copy(alpha = 0f), color)
        }
    }

    // Создаем кисть для вертикального градиента.
    val brush = remember(colors, startYPercentage, endYPercentage) {
        Brush.verticalGradient(
            colors = if (startYPercentage < endYPercentage) colors else colors.reversed()
        )
    }

    // Рисуем градиент на заднем плане.
    drawBehind {
        val topLeft = Offset(0f, size.height * min(startYPercentage, endYPercentage))
        val bottomRight = Offset(size.width, size.height * max(startYPercentage, endYPercentage))

        drawRect(
            topLeft = topLeft,
            size = Rect(topLeft, bottomRight).size,
            brush = brush
        )
    }
}