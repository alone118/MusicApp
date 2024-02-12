package com.example.musicapp.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import com.example.musicapp.data.cache.models.artists.ArtistEntity
import com.example.musicapp.domain.repositories.ArtistRepository
import javax.inject.Inject

/**
 * Реализация интерфейса [ArtistRepository], ответственная за получение
 * информации об исполнителях и их альбомах из внешнего провайдера контента
 * устройства с использованием [ContentResolver].
 *
 * @property contentResolver [ContentResolver], используемый для запросов к медиа-контенту устройства.
 * @property artists Список объектов [ArtistEntity], представляющих полученных исполнителей.
 * @property artistAlbums Мутабельная карта, где ключ - ID исполнителя, а значение - список ID альбомов,
 * связанных с этим исполнителем.
 */

class ArtistRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ArtistRepository {

    // Список для хранения полученных исполнителей
    var artists = emptyList<ArtistEntity>()
        private set

    // Карта для хранения соответствия ID исполнителя и ID связанных с ним альбомов
    var artistAlbums = mutableMapOf<String, List<String>>()
        private set


    // Получает список исполнителей из внешнего провайдера контента и заполняет
    // список [artists]. Кроме того, для каждого исполнителя вызывает
    // метод [fetchArtistAlbums] для получения связанных альбомов.
    override suspend fun fetchArtists() {
        // Временный список для хранения полученных исполнителей
        val artistsList = mutableListOf<ArtistEntity>()
        // Проекция для запроса информации об исполнителях
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST
        )

        // Запрос внешнего провайдера контента для получения информации об исполнителях
        contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Audio.Artists.ARTIST
        )?.use { cursor ->
            // Индексы столбцов для информации об исполнителях
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

            // Итерация по курсору для получения деталей исполнителей
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val artistName = cursor.getString(artistColumn)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, id)
                val name =
                    if (artistName == MediaStore.UNKNOWN_STRING) "Unknown Artist" else artistName
                artistsList += ArtistEntity(id, uri.toString(), name)
            }
        }
        // Запуск получения альбомов для каждого исполнителя
        artistsList.forEach { fetchArtistAlbums(it.id.toString()) }
        // Удаление исполнителей без связанных альбомов
        artistsList.removeIf { artistAlbums[it.id.toString()]?.isEmpty() == true }
        // Обновление списка исполнителей полученными данными
        artists = artistsList
    }

    // Получает список ID альбомов, связанных с определенным ID исполнителя, и обновляет
    // карту [artistAlbums].
    // @param artistId ID исполнителя, для которого нужно получить альбомы.
    override suspend fun fetchArtistAlbums(artistId: String) {
        // Пропуск получения альбомов, если ID исполнителя недействителен
        if (artistId.toLong() == -1L) return

        // Список для хранения ID альбомов, связанных с исполнителем
        val albumIds = mutableListOf<String>()
        // Проекция для запроса информации об альбомах
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID
        )

        // Запрос внешнего провайдера контента для получения ID альбомов, связанных с исполнителем
        contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Audio.Albums.ARTIST_ID} = ?",
            arrayOf(artistId),
            null,
        )?.use { cursor ->
            // Индекс столбца для ID альбомов
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)

            // Итерация по курсору для получения ID альбомов
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                albumIds += id.toString()
            }
            // Обновление карты [artistAlbums] полученными ID альбомов
            artistAlbums[artistId] = albumIds
        }
    }
}