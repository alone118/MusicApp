package com.example.musicapp.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.example.musicapp.data.cache.models.musics.MusicEntity
import com.example.musicapp.domain.repositories.MusicRepository
import javax.inject.Inject

/**
 * Реализация интерфейса [MusicRepository], отвечающая за получение информации
 * о музыкальных композициях из внешнего контента устройства с использованием [ContentResolver].
 *
 * @property contentResolver [ContentResolver], используемый для запросов к внешнему медиа-контенту устройства.
 */

class MusicRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : MusicRepository {

    // Список для хранения полученных музыкальных композиций
    fun musics(): List<MusicEntity> = musics.toList()

    private var musics = emptyList<MusicEntity>()

    // Массив для проекции, определяющий необходимые атрибуты музыкальных композиций
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATA,
    )

    // Получает список музыкальных композиций из внешнего контента устройства.
    // Каждая композиция представляется объектом [MusicCache], который содержит
    // информацию о ней.
    override suspend fun fetchMusics() {
        val musicsList = mutableListOf<MusicEntity>()
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} = ?",
            arrayOf("1"),
            null
        )?.use { cursor ->
            // Индексы столбцов для информации о музыке
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackNumberColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            // Итерация по курсору для получения информации о музыке
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val album = cursor.getString(albumColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val track = cursor.getInt(trackNumberColumn)
                val path = cursor.getString(dataColumn)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                        .toString()
                val albumName = if (album == MediaStore.UNKNOWN_STRING) "Unknown Album" else album
                val artistName =
                    if (artist == MediaStore.UNKNOWN_STRING) "Unknown Artist" else artist
                val trackNumber = if (track > 1000) track % 1000 else track
                musicsList += MusicEntity(
                    id = id,
                    uri = uri,
                    title = title,
                    albumId = albumId,
                    album = albumName,
                    artistId = artistId,
                    artist = artistName,
                    duration = duration,
                    trackNumber = trackNumber,
                    path = path
                )
            }
        }
        // Обновление списка музыкальных композиций полученными данными
        musics = musicsList
    }
}