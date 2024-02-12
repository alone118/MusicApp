package com.example.musicapp.data.cache.service

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.annotation.OptIn
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.example.musicapp.data.repositories.AlbumsRepositoryImpl
import com.example.musicapp.data.repositories.ArtistRepositoryImpl
import com.example.musicapp.data.repositories.MusicRepositoryImpl
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.ArtistRepository
import com.example.musicapp.domain.repositories.MusicRepository

/**
 * Класс, представляющий дерево медиа-элементов приложения.
 *
 * @property musicRepository Репозиторий музыкальных треков.
 * @property albumsRepository Репозиторий альбомов.
 * @property artistRepository Репозиторий артистов.
 */

// Идентификатор корневого узла в дереве медиа-элементов.
const val ROOT_ID = "ROOT_ID"
// Идентификатор узла медиа-элементов для музыкальных треков.
const val MUSICS_ID = "MUSICS_ID"
// Идентификатор узла медиа-элементов для альбомов.
const val ALBUMS_ID = "ALBUMS_ID"
// Идентификатор узла медиа-элементов для артистов.
const val ARTISTS_ID = "ARTISTS_ID"

@OptIn(UnstableApi::class)
class MediaItemTree(
    musicRepository: MusicRepository,
    albumsRepository: AlbumsRepository,
    artistRepository: ArtistRepository
) {

    // Map для хранения узлов дерева медиа-элементов.
    private val treeNode = mutableMapOf<String, MediaItemNode>()

    init {
        // Инициализация корневых узлов дерева.
        treeNode[ROOT_ID] = MediaItemNode(
            buildMediaItem(ROOT_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[MUSICS_ID] = MediaItemNode(
            buildMediaItem(MUSICS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ALBUMS_ID] = MediaItemNode(
            buildMediaItem(ALBUMS_ID, isBrowsable = true, isPlayable = false)
        )
        treeNode[ARTISTS_ID] = MediaItemNode(
            buildMediaItem(ARTISTS_ID, isBrowsable = true, isPlayable = false)
        )
        // Добавление дочерних узлов к корневому узлу.
        treeNode[ROOT_ID]!!.addChild(MUSICS_ID)
        treeNode[ROOT_ID]!!.addChild(ALBUMS_ID)
        treeNode[ROOT_ID]!!.addChild(ARTISTS_ID)
        // Добавление музыкальных треков в дерево.
        (musicRepository as MusicRepositoryImpl).musics().forEach { music ->
            val id = music.id.toString()
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = music.uri,
                    isBrowsable = false,
                    isPlayable = true,
                    title = music.title,
                    album = music.album,
                    artist = music.artist,
                    trackNumber = music.trackNumber,
                    artworkUri = Uri.parse(music.uri),
                    artworkData = getArtworkData(music.path)
                )
            )
            treeNode[MUSICS_ID]!!.addChild(id)
        }

        // Добавление альбомов в дерево.
        (albumsRepository as AlbumsRepositoryImpl).albums.forEach { album ->
            val id = album.id.toString()
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = album.uri,
                    isBrowsable = true,
                    isPlayable = false,
                    title = album.name,
                    artist = album.artist,
                    artworkUri = Uri.parse(album.artworkUri),
                    totalTrackCount = album.noOfMusics,
                    recordingYear = album.year
                )
            )
            treeNode[ALBUMS_ID]!!.addChild(id)

            // Добавление музыкальных треков, принадлежащих альбому, в дерево.
            val musics = albumsRepository.albumMusics[id] ?: emptyList()
            musics.forEach { music ->
                val item = treeNode[music.id.toString()]?.mediaItem
                if (item != null) {
                    treeNode[id]!!.addChild(item.mediaId)
                }
            }
        }

        // Добавление артистов в дерево.
        (artistRepository as ArtistRepositoryImpl).artists.forEach { artist ->
            val id = "artist-${artist.id}"
            treeNode[id] = MediaItemNode(
                buildMediaItem(
                    mediaId = id,
                    uri = artist.uri,
                    isBrowsable = true,
                    isPlayable = false,
                    title = artist.name
                )
            )
            treeNode[ARTISTS_ID]!!.addChild(id)

            // Добавление идентификаторов альбомов, принадлежащих артисту, в дерево.
            val albumIds = artistRepository.artistAlbums[artist.id.toString()] ?: emptyList()
            albumIds.forEach { albumId ->
                val item = treeNode[albumId]?.mediaItem
                if (item != null) {
                    treeNode[id]!!.addChild(item.mediaId)
                }
            }
        }
    }

/**
* Внутренний класс, представляющий узел в дереве медиа-элементов.
*
* @property mediaItem Медиа-элемент, представленный данным узлом.
*/

    inner class MediaItemNode(val mediaItem: MediaItem) {
        private val children: MutableList<MediaItem> = mutableListOf()

        // Добавляет идентификатор дочернего элемента к текущему узлу.
        // childId: Идентификатор дочернего элемента.
        fun addChild(childId: String) {
            children.add(treeNode[childId]!!.mediaItem)
        }

        // Возвращает список дочерних медиа-элементов данного узла.
        fun getChildren(): List<MediaItem> {
            return children
        }
    }

/**
* Получает узел медиа-элемента по его идентификатору.
*
* @param id Идентификатор медиа-элемента.
* @return Узел медиа-элемента или `null`, если элемент не найден.
*/

    operator fun get(id: String): MediaItemNode? = treeNode[id]

    /**
     * Создает объект MediaItem с заданными метаданными.
     *
     * @param mediaId Идентификатор медиа-элемента.
     * @param isBrowsable Указывает, является ли медиа-элемент проходимым (навигируемым).
     * @param isPlayable Указывает, является ли медиа-элемент воспроизводимым.
     * @param uri URI медиа-элемента.
     * @param title Название медиа-элемента.
     * @param album Альбом, к которому относится медиа-элемент.
     * @param artist Исполнитель медиа-элемента.
     * @param artworkUri URI обложки медиа-элемента.
     * @param trackNumber Номер трека, если применимо.
     * @param artworkData Данные об обложке медиа-элемента в виде массива байт.
     * @param totalTrackCount Общее количество треков в альбоме, если применимо.
     * @param recordingYear Год записи медиа-элемента.
     * @return Созданный объект MediaItem.
     */

    private fun buildMediaItem(
        mediaId: String,
        isBrowsable: Boolean,
        isPlayable: Boolean,
        uri: String? = null,
        title: String? = null,
        album: String? = null,
        artist: String? = null,
        artworkUri: Uri? = null,
        trackNumber: Int? = null,
        artworkData: ByteArray? = null,
        totalTrackCount: Int? = null,
        recordingYear: Int? = null
    ): MediaItem {

        // Строит метаданные медиа-элемента с использованием MediaMetadata.Builder
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(title)
            .setAlbumTitle(album)
            .setArtist(artist)
            .setArtworkUri(artworkUri)
            .setArtworkData(artworkData, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
            .setTrackNumber(trackNumber)
            .setIsBrowsable(isBrowsable)
            .setIsPlayable(isPlayable)
            .setTotalTrackCount(totalTrackCount)
            .setRecordingYear(recordingYear)
            .build()

        // Создает объект MediaItem с помощью MediaItem.Builder
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setUri(uri)
            .setMediaMetadata(mediaMetadata)
            .build()
    }

/**
* Возвращает медиа-элемент по его идентификатору.
*
* @param id Идентификатор медиа-элемента.
* @return Медиа-элемент или `null`, если элемент не найден.
*/

    fun getMediaItem(id: String): MediaItem? {
        return treeNode[id]?.mediaItem
    }

/**
* Получает данные обложки альбома в виде массива байтов из указанного пути к файлу.
*
* @param path Путь к файлу с аудиоданными.
* @return Массив байтов с данными обложки альбома или `null`, если данные отсутствуют.
*/

    private fun getArtworkData(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }
}