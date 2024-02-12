package com.example.musicapp.data.mappers

import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.models.Playlist
import com.example.musicapp.domain.models.AlbumDomain
import com.example.musicapp.domain.models.MusicDomain
import com.example.musicapp.domain.models.PlaylistDomain

fun Music.toDomain(): MusicDomain = MusicDomain(
    id = id,
    title = title,
    uri = uri,
    artist = artist,
    album = album,
    trackNumber = trackNumber,
    artworkData = artworkData,
    artworkUri = artworkUri
)

fun AlbumDomain.toData(): Album = Album(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)