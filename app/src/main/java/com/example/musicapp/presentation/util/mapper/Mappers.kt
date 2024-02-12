package com.example.musicapp.presentation.util.mapper

import androidx.media3.common.MediaItem
import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Artist
import com.example.musicapp.data.models.Music
import com.example.musicapp.domain.models.AlbumDomain
import com.example.musicapp.domain.models.MusicDomain

fun MediaItem.toAlbum(): Album {
    return Album(
        id = mediaId.toLong(),
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString(),
        artist = mediaMetadata.artist.toString(),
        artworkUri = mediaMetadata.artworkUri.toString(),
        noOfMusics = mediaMetadata.totalTrackCount!!,
        year = mediaMetadata.recordingYear!!
    )
}

fun MediaItem.toMusic(): Music {
    return Music(
        id = mediaId.toLong(),
        uri = localConfiguration?.uri.toString(),
        title = mediaMetadata.title.toString(),
        album = mediaMetadata.albumTitle.toString(),
        artist = mediaMetadata.artist.toString(),
        trackNumber = mediaMetadata.trackNumber!!,
        artworkUri = mediaMetadata.artworkUri.toString(),
        artworkData = mediaMetadata.artworkData
    )
}

fun MediaItem.toArtist(): Artist {
    return Artist(
        id = mediaId,
        uri = localConfiguration?.uri.toString(),
        name = mediaMetadata.title.toString(),
    )
}

fun Album.toDomain(): AlbumDomain = AlbumDomain(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)

fun Music.toDomain(): MusicDomain = MusicDomain(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
)