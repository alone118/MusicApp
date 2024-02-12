package com.example.musicapp.domain.repositories

/**
 * Интерфейс [ArtistRepository] предоставляет методы для получения данных об артистах и их альбомах.
 * Репозиторий отвечает за взаимодействие с внешними источниками данных для получения информации о музыкальных артистах.
 */

interface ArtistRepository {

    // Асинхронно выполняет запрос для получения данных об артистах.
    suspend fun fetchArtists()

    // Асинхронно выполняет запрос для получения данных об альбомах конкретного артиста.
    // artistId: Идентификатор артиста.
    suspend fun fetchArtistAlbums(artistId: String)
}