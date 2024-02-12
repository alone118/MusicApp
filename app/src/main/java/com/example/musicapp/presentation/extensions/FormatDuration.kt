package com.example.musicapp.presentation.extensions

import java.util.concurrent.TimeUnit

/**
 * Форматирует длительность времени в формат "минуты:секунды".
 * Преобразует длительность из миллисекунд в минуты и секунды, затем форматирует их в виде строки.
 *
 * @param duration Длительность времени в миллисекундах.
 * @return Отформатированная строка длительности времени в формате "мм:сс".
 */

fun formatDuration(duration: Long): String {
    // Преобразование длительности из миллисекунд в минуты.
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
    // Преобразование длительности из миллисекунд в общее количество секунд.
    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(duration)
    // Определение количества секунд, оставшихся после вычитания минут.
    val remainingSeconds = totalSeconds - (60 * minutes)
    // Форматирование минут и секунд в формат "мм:сс".
    return String.format("%02d:%02d", minutes, remainingSeconds)
}