package com.example.musicapp.data.repositories

import com.example.musicapp.data.cache.dao.RecentlyPlayedDao
import com.example.musicapp.data.mappers.toEntity
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.domain.models.RecentlyPlayedDomain
import com.example.musicapp.domain.repositories.RecentlyPlayedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация интерфейса [RecentlyPlayedRepository], отвечающая за взаимодействие
 * с недавно воспроизведенными музыкальными композициями через [RecentlyPlayedDao].
 *
 * @property recentlyPlayedDao [RecentlyPlayedDao], используемый для доступа к базе данных
 * недавно воспроизведенных композиций.
 */

class RecentlyPlayedRepositoryImpl @Inject constructor(
    private val recentlyPlayedDao: RecentlyPlayedDao
) : RecentlyPlayedRepository {

    // Получает список недавно воспроизведенных музыкальных композиций в виде потока данных [Flow].
    // Каждая композиция преобразуется в [RecentlyPlayedDomain] с использованием функции расширения [toDomain].
    // @return [Flow] с списком [RecentlyPlayedDomain].
    override fun getRecentlyPlayed(): Flow<List<RecentlyPlayedDomain>> {
        return recentlyPlayedDao.getRecentlyPlayed().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Добавляет новую музыкальную композицию в список недавно воспроизведенных.
    // @param recentlyPlayed [RecentlyPlayedDomain], представляющий добавляемую композицию.
    override suspend fun addRecentlyPlayed(recentlyPlayed: RecentlyPlayedDomain) {
        recentlyPlayedDao.addRecentlyPlayed(recentlyPlayed.toEntity())
    }
}