package com.example.musicapp.domain.models

data class AlbumDomain (
    val id: Long,
    val uri: String,
    val name:String,
    val artist: String,
    val artworkUri: String,
    val noOfMusics: Int,
    val year: Int
)