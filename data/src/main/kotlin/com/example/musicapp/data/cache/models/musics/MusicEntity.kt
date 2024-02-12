package com.example.musicapp.data.cache.models.musics

data class MusicEntity(
    val id: Long,
    val uri: String,
    val title: String,
    val artistId: Long,
    val artist: String,
    val albumId: Long,
    val album: String,
    val trackNumber: Int,
    val path: String,
    val duration: Long,
){
    companion object {
        val unknown = MusicEntity(
            id = -1L,
            uri = "",
            title = "",
            artistId = 0L,
            artist = "",
            albumId = 0L,
            album = "",
            trackNumber = 0,
            path = "",
            duration = 0L,
        )
    }
}