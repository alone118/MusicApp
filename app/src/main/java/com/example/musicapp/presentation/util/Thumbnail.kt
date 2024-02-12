package com.example.musicapp.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi

/**
 * Получает миниатюру изображения для указанного URI и возвращает её в виде объекта Bitmap.
 * @param context Контекст приложения.
 * @param artworkUri URI изображения, для которого нужно получить миниатюру.
 * @return Объект Bitmap с миниатюрой изображения или null, если не удалось загрузить.
 */

@RequiresApi(Build.VERSION_CODES.Q)
fun getThumbnail(context: Context, artworkUri: String): Bitmap? {
    return try {
        context.contentResolver.loadThumbnail(
            Uri.parse(artworkUri),
            Size(300, 300),
            null
        )
    } catch (e: Exception) {
        null
    }
}