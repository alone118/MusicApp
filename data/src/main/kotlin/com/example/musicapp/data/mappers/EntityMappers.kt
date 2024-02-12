package com.example.musicapp.data.mappers

import com.example.musicapp.data.cache.models.albums.AlbumEntity
import com.example.musicapp.data.cache.models.albums.TopAlbumsEntity
import com.example.musicapp.data.cache.models.favourites.FavouriteMusicEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistEntity
import com.example.musicapp.data.cache.models.playlist.PlaylistMusicEntity
import com.example.musicapp.data.cache.models.recently_played.RecentlyPlayedEntity
import com.example.musicapp.data.models.Album
import com.example.musicapp.data.models.Music
import com.example.musicapp.data.models.Playlist
import com.example.musicapp.domain.models.AlbumDomain
import com.example.musicapp.domain.models.FavouriteMusicsDomain
import com.example.musicapp.domain.models.MusicDomain
import com.example.musicapp.domain.models.PlaylistDomain
import com.example.musicapp.domain.models.RecentlyPlayedDomain
import com.example.musicapp.domain.models.TopAlbumsDomain

fun PlaylistDomain.toEntity(): PlaylistEntity = PlaylistEntity(
    id = id,
    name = name
)

fun PlaylistEntity.toDomain(): PlaylistDomain = PlaylistDomain(
    id = id,
    name = name
)

fun TopAlbumsEntity.toDomain(): TopAlbumsDomain = TopAlbumsDomain(
    albumId = albumId,
    totalPlayCount = totalPlayCount
)

fun TopAlbumsDomain.toEntity(): TopAlbumsEntity = TopAlbumsEntity(
    albumId = albumId,
    totalPlayCount = totalPlayCount
)

fun RecentlyPlayedEntity.toDomain(): RecentlyPlayedDomain = RecentlyPlayedDomain(
    musicId = musicsId,
    time = time
)

fun RecentlyPlayedDomain.toEntity(): RecentlyPlayedEntity = RecentlyPlayedEntity(
    musicsId = musicId,
    time = time
)

fun AlbumEntity.toDomain(): AlbumDomain = AlbumDomain(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)

fun AlbumEntity.toUi(): Album = Album(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)

fun AlbumDomain.toEntity(): AlbumEntity = AlbumEntity(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)

fun Album.toDomain(): AlbumDomain = AlbumDomain(
    id = id,
    uri = uri,
    name = name,
    artist = artist,
    artworkUri = artworkUri,
    noOfMusics = noOfMusics,
    year = year
)

fun Playlist.toDomain(): PlaylistDomain = PlaylistDomain(
    id = id,
    name = name
)

fun PlaylistDomain.toData(): Playlist = Playlist(
    id = id,
    name = name
)

fun PlaylistMusicEntity.toDomain(): MusicDomain = MusicDomain(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
)

fun MusicDomain.toEntity(): PlaylistMusicEntity = PlaylistMusicEntity(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
)

fun MusicDomain.toUi(): Music = Music(
    id = id,
    uri = uri,
    title = title,
    artist = artist,
    album = album,
    trackNumber = trackNumber,
    artworkUri = artworkUri,
    artworkData = artworkData
)

fun FavouriteMusicsDomain.toEntity(): FavouriteMusicEntity = FavouriteMusicEntity(
    musicId = musicId
)

fun FavouriteMusicEntity.toDomain(): FavouriteMusicsDomain = FavouriteMusicsDomain(
    musicId = musicId
)