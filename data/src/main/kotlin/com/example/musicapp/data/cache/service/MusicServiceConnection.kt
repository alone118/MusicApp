package com.example.musicapp.data.cache.service

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Events
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Класс для управления соединением с [MusicService] через [MediaBrowser].
 *
 * @property context Контекст приложения.
 */

class MusicServiceConnection @Inject constructor(
    @ApplicationContext context: Context
) {

    // Токен сеанса для MediaBrowser.
    private val sessionToken =
        SessionToken(context, ComponentName(context, MusicService::class.java))

    // Фьючерс для построения MediaBrowser.
    private val browserFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()

    // Текущий MediaBrowser.
    val mediaBrowser: MutableStateFlow<MediaBrowser?> = MutableStateFlow(null)

    // Состояние воспроизведения текущего трека.
    val nowPlaying: MutableStateFlow<MediaItem> = MutableStateFlow(MediaItem.EMPTY)

    // Флаг, указывающий, проигрывается ли музыка в данный момент.
    val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)

    // Продолжительность текущего трека.
    val duration: MutableStateFlow<Long> = MutableStateFlow(0L)

    init {
        // Добавление слушателя при завершении построения MediaBrowser.
        browserFuture.addListener(
            {
                val browser = browserFuture.get()
                mediaBrowser.update {
                    browser
                }
                browser.addListener(playerListener)
            },
            MoreExecutors.directExecutor()
        )
    }

    /**
     * Получить дочерние элементы для указанного родителя.
     *
     * @param parentId Идентификатор родителя.
     * @return Список дочерних элементов [MediaItem].
     */

    fun getChildren(parentId: String): List<MediaItem> {
        val browser = mediaBrowser.value ?: return emptyList()
        val items = mutableListOf<MediaItem>()
        val childrenFuture = browser.getChildren(parentId, 0, 100, null)
        childrenFuture.addListener(
            {
                val children = childrenFuture.get().value ?: emptyList()
                items.addAll(children)
            },
            MoreExecutors.directExecutor()
        )
        return ImmutableList.copyOf(items)
    }

    /**
     * Получить [MediaItem] для указанного идентификатора медиа.
     *
     * @param mediaId Идентификатор медиа.
     * @return [MediaItem].
     */

    fun getMediaItem(mediaId: String): MediaItem {
        val browser = mediaBrowser.value ?: return MediaItem.EMPTY
        var item = MediaItem.EMPTY
        val itemFuture = browser.getItem(mediaId)
        itemFuture.addListener(
            {
                item = itemFuture.get().value ?: MediaItem.EMPTY
            },
            MoreExecutors.directExecutor()
        )
        return item
    }

    // Слушатель событий проигрывателя.
    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Events) {
            // Обработка события перехода к новому элементу медиа.
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)) {
                player.currentMediaItem?.let { mediaItem ->
                    nowPlaying.update {
                        mediaItem
                    }
                }
            }

            // Обработка изменения состояния проигрывания (воспроизводится/пауза).
            if (events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                isPlaying.update {
                    player.isPlaying
                }
            }

            // Обработка событий, связанных с изменением проигрывания (воспроизведение, пауза, стоп).
            if (events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
            ) {
                // Проверка, что длительность трека больше 0 (трек проигрывается).
                if (player.duration > 0) {
                    duration.update {
                        player.duration
                    }
                }
            }
        }
    }

    // Освободить ресурсы и удалить слушателя при завершении работы с MediaBrowser.
    fun releaseBrowser() {
        mediaBrowser.value?.removeListener(playerListener)
        MediaBrowser.releaseFuture(browserFuture)
    }
}