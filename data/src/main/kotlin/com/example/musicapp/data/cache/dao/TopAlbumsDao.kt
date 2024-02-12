package com.example.musicapp.data.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapp.data.cache.models.albums.TopAlbumsEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) для управления данными о топовых альбомах в локальной базе данных.
 * Предоставляет методы для получения списка топовых альбомов, информации о конкретном топовом альбоме,
 * добавления нового топового альбома и удаления топового альбома.
 */

@Dao
interface TopAlbumsDao {

    // Получает все альбомы из топа, отсортированные по общему количеству проигрываний в порядке убывания.
    @Query("SELECT * FROM top_albums ORDER BY total_play_count DESC")
    fun getTopAlbums(): Flow<List<TopAlbumsEntity>>

    // Получает информацию об альбоме из топа по его идентификатору.
    // albumId: Идентификатор альбома
    // return: Сущность альбома из топа или null, если альбом не найден
    @Query("SELECT * FROM top_albums WHERE id = :albumId")
    suspend fun getTopAlbum(albumId: Long): TopAlbumsEntity?

    // Добавляет новый альбом в топ или заменяет существующий.
    // topAlbumsEntity: Сущность альбома для добавления в топ
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTopAlbum(topAlbumsEntity: TopAlbumsEntity)

    // Удаляет альбом из топа по его идентификатору.
    // albumId: Идентификатор альбома, который нужно удалить из топа
    @Query("DELETE FROM top_albums WHERE id = :albumId")
    suspend fun deleteTopAlbum(albumId: Long)
}