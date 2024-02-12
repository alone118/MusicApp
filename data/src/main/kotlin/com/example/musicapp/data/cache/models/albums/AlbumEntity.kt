package com.example.musicapp.data.cache.models.albums

data class AlbumEntity(
    val id: Long,
    val uri: String,
    val name: String,
    val artist: String,
    val artworkUri: String,
    val noOfMusics: Int,
    val year: Int
)