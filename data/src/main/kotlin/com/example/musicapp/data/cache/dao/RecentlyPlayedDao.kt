package com.example.musicapp.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapp.data.cache.models.recently_played.RecentlyPlayedEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для управления данными о недавно проигранных музыкальных композициях в локальной базе данных.
 * Предоставляет методы для получения списка недавно проигранных композиций и добавления новой записи о проигрывании.
 */

@Dao
interface RecentlyPlayedDao {

    // Получает все недавно проигранные треки, отсортированные по времени в обратном порядке.
    @Query("SELECT * FROM recently_played ORDER BY time DESC")
    fun getRecentlyPlayed(): Flow<List<RecentlyPlayedEntity>>

    // Добавляет новую запись о недавно проигранном треке в базу данных или заменяет существующую.
    // entity: Сущность, представляющая недавно проигранный трек
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentlyPlayed(entity: RecentlyPlayedEntity)
}