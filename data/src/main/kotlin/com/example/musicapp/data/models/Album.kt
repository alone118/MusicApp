package com.example.musicapp.data.models

data class Album (
    val id: Long,
    val uri: String,
    val name:String,
    val artist: String,
    val artworkUri: String,
    val noOfMusics: Int,
    val year: Int
)