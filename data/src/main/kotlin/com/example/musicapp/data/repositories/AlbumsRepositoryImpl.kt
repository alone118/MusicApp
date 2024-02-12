package com.example.musicapp.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.musicapp.data.cache.models.albums.AlbumEntity
import com.example.musicapp.data.cache.models.musics.MusicEntity
import com.example.musicapp.domain.repositories.AlbumsRepository
import javax.inject.Inject

/**
 * Реализация интерфейса [AlbumsRepository] для получения данных о музыкальных альбомах.
 *
 * @property contentResolver Объект ContentResolver для взаимодействия с контентом приложения.
 */

class AlbumsRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : AlbumsRepository {

    // Мап для хранения музыки по альбомам
    var albumMusics: MutableMap<String, List<MusicEntity>> = mutableMapOf<String, List<MusicEntity>>()
        private set

    // Список для хранения информации об альбомах
    var albums = emptyList<AlbumEntity>()
        private set

    // Поля, которые мы запрашиваем из MediaStore
    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST_ID,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATA,
    )

    // Метод для получения списка альбомов из медиа-хранилища
    override suspend fun fetchAlbums() {
        val albumsList = mutableListOf<AlbumEntity>()
        // Проекция для запроса альбомов
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR,
        )

        contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Albums.ALBUM
        )?.use { cursor ->
            // Индексы столбцов в результирующем наборе данных
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val noOfMusicsColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val firstYearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)

            // Итерация по результатам запроса
            while (cursor.moveToNext()) {
                // Извлечение данных об альбоме из курсора
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(albumColumn)
                val artist = cursor.getString(artistColumn)
                val noOfMusics = cursor.getInt(noOfMusicsColumn)
                val year = cursor.getInt(firstYearColumn)
                // Формирование URI для обложки альбома
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, id)
                val artworkUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    uri
                else ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    id
                )
                // Добавление данных об альбоме в список
                albumsList += AlbumEntity(
                    id,
                    uri.toString(),
                    name,
                    artist,
                    artworkUri.toString(),
                    noOfMusics,
                    year
                )
            }
        }
        // Получение музыкальных треков для каждого альбома
        albumsList.forEach {
            fetchMusicsForAlbum(it.id)
        }
        // Установка полученных альбомов в репозиторий
        albums = albumsList
    }

    // Метод для получения списка музыкальных треков для конкретного альбома
    override suspend fun fetchMusicsForAlbum(albumId: Long) {
        val musicsList = mutableListOf<MusicEntity>()
        contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} = ? AND ${MediaStore.Audio.Media.ALBUM_ID} = ?",
            arrayOf("1", "$albumId"),
            MediaStore.Audio.Media.TRACK
        )?.use { cursor ->
            // Индексы столбцов в результирующем наборе данных
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackNumberColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val album = cursor.getString(albumColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getLong(durationColumn)
                val trackNumber = cursor.getInt(trackNumberColumn)
                val path = cursor.getString(dataColumn)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                        .toString()
                // Приведение неизвестных значений к установленным константам
                val albumName =
                    if (album == MediaStore.UNKNOWN_STRING) "Unknown Album" else album
                val artistName =
                    if (artist == MediaStore.UNKNOWN_STRING) "Unknown Artist" else artist
                // Добавление данных о музыкальном треке в список
                musicsList += MusicEntity(
                    id = id,
                    uri = uri,
                    title = title,
                    artistId = artistId,
                    album = albumName,
                    albumId = albumId,
                    artist = artistName,
                    duration = duration,
                    trackNumber = trackNumber,
                    path = path,
                )
            }
        }
        // Сохранение списка музыкальных треков в мап
        albumMusics[albumId.toString()] = musicsList.toList()
    }
}