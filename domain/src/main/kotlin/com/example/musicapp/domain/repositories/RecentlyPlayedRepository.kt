package com.example.musicapp.domain.repositories

import com.example.musicapp.domain.models.RecentlyPlayedDomain
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс [RecentlyPlayedRepository] предоставляет методы для работы с недавно проигранными музыкальными композициями.
 * Репозиторий отвечает за получение списка недавно проигранных композиций и добавление новых записей.
 */

interface RecentlyPlayedRepository {

    // Получение списка недавно проигранных музыкальных композиций в виде [Flow].
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedDomain>>

    // Асинхронно добавляет новую запись о недавно проигранной музыкальной композиции.
    // recentlyPlayed: Объект [RecentlyPlayedDomain], представляющий информацию о недавно проигранной композиции.
    suspend fun addRecentlyPlayed(recentlyPlayed: RecentlyPlayedDomain)
}