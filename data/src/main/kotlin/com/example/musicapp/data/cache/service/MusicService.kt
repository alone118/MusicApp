package com.example.musicapp.data.cache.service

import android.os.Bundle
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ControllerInfo
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import androidx.media3.session.SessionResult.RESULT_SUCCESS
import com.example.musicapp.domain.repositories.AlbumsRepository
import com.example.musicapp.domain.repositories.ArtistRepository
import com.example.musicapp.domain.repositories.MusicRepository
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Сервис управления медиабиблиотекой для воспроизведения музыки.
 */

@AndroidEntryPoint
class MusicService : MediaLibraryService() {

    // Сопровождающая работа сервиса.
    private val serviceJob = SupervisorJob()

    // Область корутин с основным диспетчером.
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // Репозиторий для работы с музыкой.
    @Inject
    lateinit var musicRepository: MusicRepository

    // Репозиторий для работы с альбомами.
    @Inject
    lateinit var albumsRepository: AlbumsRepository

    // Репозиторий для работы с артистами.
    @Inject
    lateinit var artistRepository: ArtistRepository

    // Древо медиаэлементов.
    val mediaItemTree: MediaItemTree by lazy {
        MediaItemTree(musicRepository, albumsRepository, artistRepository)
    }

    // Плеер ExoPlayer.
    private lateinit var player: ExoPlayer

    // Сессия медиабиблиотеки.
    private lateinit var mediaLibrarySession: MediaLibrarySession

    // Обработчик создания сервиса.
    override fun onCreate() {
        super.onCreate()

        // Инициализация данных о музыке, альбомах и артистах в корутине.
        serviceScope.launch {
            musicRepository.fetchMusics()
            albumsRepository.fetchAlbums()
            artistRepository.fetchArtists()
        }
        // Создание экземпляра ExoPlayer.
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                true
            )
            .build()
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback).build()
    }

    // Обработчик уничтожения сервиса.
    override fun onDestroy() {
        // Отмена сопровождающей работы при уничтожении сервиса.
        serviceScope.cancel()
        // Освобождение ресурсов, связанных с ExoPlayer и сессией медиабиблиотеки.
        mediaLibrarySession.apply {
            player.release()
            release()
        }
        super.onDestroy()
    }

    // Обработчик запроса сессии медиабиблиотеки.
    override fun onGetSession(controllerInfo: ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    // Обработчик событий сессии медиабиблиотеки.
    private val librarySessionCallback = object : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            // Добавление кастомных команд: включение/выключение режима случайного воспроизведения и режима повтора.
            availableSessionCommands.add(COMMAND_SHUFFLE_MODE_ON)
            availableSessionCommands.add(COMMAND_SHUFFLE_MODE_OFF)
            availableSessionCommands.add(COMMAND_REPEAT_MODE_OFF)
            availableSessionCommands.add(COMMAND_REPEAT_MODE_ALL)
            availableSessionCommands.add(COMMAND_REPEAT_MODE_ONE)
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        // Обработчик пользовательских команд.
        override fun onCustomCommand(
            session: MediaSession,
            controller: ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            // Обработка команд изменения режима случайного воспроизведения и режима повтора.
            when (customCommand.customAction) {
                SHUFFLE_MODE_ON -> player.shuffleModeEnabled = true
                SHUFFLE_MODE_OFF -> player.shuffleModeEnabled = false
                REPEAT_MODE_OFF -> player.repeatMode = Player.REPEAT_MODE_OFF
                REPEAT_MODE_ALL -> player.repeatMode = Player.REPEAT_MODE_ALL
                REPEAT_MODE_ONE -> player.repeatMode = Player.REPEAT_MODE_ONE
            }
            // Возвращение успешного результата.
            return Futures.immediateFuture(SessionResult(RESULT_SUCCESS))
        }

        // Обработчик запроса корневого элемента библиотеки.
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = mediaItemTree[ROOT_ID]?.mediaItem ?: return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            )
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        // Обработчик запроса элемента библиотеки по идентификатору.
        override fun onGetItem(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val mediaItem = mediaItemTree.getMediaItem(mediaId) ?: return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            )
            return Futures.immediateFuture(LibraryResult.ofItem(mediaItem, null))
        }

        // Обработчик запроса дочерних элементов библиотеки.
        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children = mediaItemTree[parentId]?.getChildren() ?: return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            )
            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }

        // Обработчик добавления элементов в библиотеку.
        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<MutableList<MediaItem>> {
            // Обновление элементов медиабиблиотеки с учетом добавленных элементов.
            val updateMediaItems =
                mediaItems.map { mediaItemTree.getMediaItem(it.mediaId) ?: it }.toMutableList()
            // Возврат обновленных элементов.
            return Futures.immediateFuture(updateMediaItems)
        }

        // Обработчик подписки на обновления библиотеки.
        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            // Обработка подписки
            return super.onSubscribe(session, browser, parentId, params)
        }

        // Обработчик отписки от обновлений библиотеки.
        override fun onUnsubscribe(
            session: MediaLibrarySession,
            browser: ControllerInfo,
            parentId: String
        ): ListenableFuture<LibraryResult<Void>> {
            // Обработка отписки
            return super.onUnsubscribe(session, browser, parentId)
        }
    }

    companion object {
        private const val SHUFFLE_MODE_ON = "SHUFFLE_MODE_ON"
        private const val SHUFFLE_MODE_OFF = "SHUFFLE_MODE_OFF"
        private const val REPEAT_MODE_OFF = "REPEAT_MODE_OFF"
        private const val REPEAT_MODE_ALL = "SHUFFLE_MODE_ALL"
        private const val REPEAT_MODE_ONE = "REPEAT_MODE_ONE"

        // Команды для управления режимом случайного воспроизведения и режимом повтора.
        val COMMAND_SHUFFLE_MODE_ON = SessionCommand(SHUFFLE_MODE_ON, Bundle.EMPTY)
        val COMMAND_SHUFFLE_MODE_OFF = SessionCommand(SHUFFLE_MODE_OFF, Bundle.EMPTY)
        val COMMAND_REPEAT_MODE_OFF = SessionCommand(REPEAT_MODE_OFF, Bundle.EMPTY)
        val COMMAND_REPEAT_MODE_ALL = SessionCommand(REPEAT_MODE_ALL, Bundle.EMPTY)
        val COMMAND_REPEAT_MODE_ONE = SessionCommand(REPEAT_MODE_ONE, Bundle.EMPTY)
    }
}