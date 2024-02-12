package com.example.musicapp.data.repositories

import android.util.Log
import com.example.musicapp.data.cache.dao.FavouriteMusicsDao
import com.example.musicapp.data.mappers.toEntity
import com.example.musicapp.data.mappers.toDomain
import com.example.musicapp.domain.models.FavouriteMusicsDomain
import com.example.musicapp.domain.repositories.FavouriteMusicsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация интерфейса [FavouriteMusicsRepository], ответственная за взаимодействие
 * с избранными музыкальными композициями в базе данных через [FavouriteMusicsDao].
 *
 * @property favouriteMusicsDao [FavouriteMusicsDao], используемый для доступа к базе данных
 * избранных музыкальных композиций.
 */

class FavouriteMusicsRepositoryImpl @Inject constructor(
    private val favouriteMusicsDao: FavouriteMusicsDao
) : FavouriteMusicsRepository {

    // Получает список избранных музыкальных композиций в виде потока данных [Flow].
    // Каждая запись из базы данных преобразуется в [FavouriteMusicsDomain] с использованием
    // функции расширения [toDomain].
    // @return [Flow] с списком [FavouriteMusicsDomain].
    override fun getFavouriteMusics(): Flow<List<FavouriteMusicsDomain>> {
        return favouriteMusicsDao.getFavouriteMusics().map { musics ->
            Log.i("KKK", "musics: $musics")
            musics.map {
                it.toDomain()
            }
        }
    }

    // Проверяет, является ли музыкальная композиция с указанным ID избранной.
    // @param musicId ID музыкальной композиции.
    // @return [Flow] с булевым значением, представляющим факт наличия композиции в избранном.
    override fun isFavourite(musicId: Long): Flow<Boolean> {
        return favouriteMusicsDao.isFavourite(musicId)
    }

    // Переключает состояние избранности музыкальной композиции с указанным ID.
    // Если композиция уже является избранной, она удаляется из списка избранных;
    // в противном случае она добавляется в избранное с использованием
    // объекта [FavouriteMusicsDomain], созданного на основе переданного ID.
    // @param musicId ID музыкальной композиции.
    override suspend fun toggleFavourite(musicId: Long) {
        val isFavourite = favouriteMusicsDao.isFavourite(musicId).first()
        if (isFavourite) deleteFavouriteMusic(musicId) else addFavouriteMusic(
            FavouriteMusicsDomain(
                musicId
            )
        )
    }

    // Добавляет музыкальную композицию в список избранных.
    // @param favouriteMusicsDomain [FavouriteMusicsDomain] для добавления в базу данных.
    override suspend fun addFavouriteMusic(favouriteMusicsDomain: FavouriteMusicsDomain) {
        favouriteMusicsDao.addFavouriteMusic(favouriteMusicsDomain.toEntity())
    }

    // Удаляет музыкальную композицию из списка избранных по её ID.
    // @param musicId ID музыкальной композиции для удаления.
    override suspend fun deleteFavouriteMusic(musicId: Long) {
        favouriteMusicsDao.deleteFavouriteMusic(musicId)
    }
}