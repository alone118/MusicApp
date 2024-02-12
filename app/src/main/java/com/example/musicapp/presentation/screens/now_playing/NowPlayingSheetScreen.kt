package com.example.musicapp.presentation.screens.now_playing

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.R
import com.example.musicapp.presentation.extensions.SpacerWight
import com.example.musicapp.presentation.extensions.verticalGradientScrim
import com.example.musicapp.presentation.screens.now_playing.components.NowPlayingDynamicTheme
import com.example.musicapp.presentation.theme.LargeSpacing
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.theme.MusicAppTheme

/**
 * Экран отображения текущего воспроизводимого трека в нижней панели.
 * Этот экран позволяет пользователю управлять воспроизведением трека, просматривать информацию о треке и показывать плейлист, если он активен.
 *
 * @param scaffoldState состояние Scaffold для управления состоянием нижней панели
 * @param modifier модификатор для управления внешним видом экрана
 * @param onSheetCollapsed функция обратного вызова, вызываемая при сворачивании панели
 * @param content контент для отображения на экране, передается в виде Composable-функции
 */

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun NowPlayingSheetScreen(
    scaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
    onSheetCollapsed: (Boolean) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    // Получение ViewModel из области видимости
    val viewModel: NowPlayingViewModel = viewModel()
    // Получение текущего состояния интерфейса из ViewModel и сохранение его в состоянии UI
    val uiState by viewModel.uiState.collectAsState()
    // Установка начального значения альфа для анимации
    var alphaValue by remember { mutableStateOf(1f) }

    // Создание BoxWithConstraints для обработки размеров и ограничений экрана
    BoxWithConstraints(modifier = modifier) {
        // Вычисление высоты панели в разных состояниях
        val closedSheetHeight = 64.dp
        val collapsedSheetHeight = with(LocalDensity.current) { closedSheetHeight.toPx() }
        val dragRange = constraints.maxHeight - collapsedSheetHeight

        // Создание BottomSheetScaffold для управления нижней панелью
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            sheetPeekHeight = closedSheetHeight,
            sheetDragHandle = null,
            sheetShape = BottomSheetDefaults.HiddenShape,
            sheetContent = {
                // Внутреннее содержимое нижней панели
                Box(modifier = Modifier.fillMaxSize()) {
                    // Применение темы приложения к экрану воспроизведения
                    MusicAppTheme(darkTheme = true) {
                        // Управление стилем статус-бара в зависимости от темы
                        val view = LocalView.current
                        val darkTheme = isSystemInDarkTheme()
                        SideEffect {
                            with(view.context as Activity) {
                                window.statusBarColor = Color.Transparent.toArgb()
                                WindowCompat.getInsetsController(
                                    window,
                                    view
                                ).isAppearanceLightStatusBars = !darkTheme
                            }
                        }
                        // Применение динамической темы в зависимости от обложки трека
                        NowPlayingDynamicTheme(artworkData = uiState.music.artworkData) {
                            // Отображение содержимого экрана воспроизведения
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = 1 - alphaValue
                                    },
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Column(
                                    modifier = Modifier
                                        .verticalGradientScrim(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.90f),
                                            startYPercentage = 1f,
                                            endYPercentage = 0f
                                        )
                                        .statusBarsPadding(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Добавление кнопки перетаскивания для сворачивания панели
                                    BottomSheetDefaults.DragHandle()
                                    // Отображение экрана воспроизведения трека
                                    NowPlayingScreen(
                                        uiState = uiState,
                                        onPositionChange = { viewModel.seekTo(it.toLong()) },
                                        playNextMusic = viewModel::playNextMusic,
                                        playPreviousMusic = viewModel::playPreviousMusic,
                                        playOrPause = viewModel::playOrPause,
                                        play = viewModel::play,
                                        togglePlaylistItems = viewModel::togglePlaylistItems,
                                        toggleShuffleMode = viewModel::toggleShuffleMode,
                                        updateRepeatMode = viewModel::updateRepeatMode,
                                        toggleFavourite = viewModel::toggleFavourite
                                    )
                                }
                            }
                        }
                    }
                    // Визуальное представление текущего трека и контроль воспроизведения
                    Row(
                        modifier = Modifier
                            .height(closedSheetHeight)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = alphaValue
                            }
                            .padding(horizontal = LargeSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Отображение обложки трека
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uiState.music.artworkData)
                                .crossfade(true)
                                .build(),
                            error = painterResource(id = R.drawable.music_note),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                        SpacerWight(MediumSpacing)
                        // Отображение названия трека с возможностью прокрутки
                        Text(
                            text = uiState.music.title,
                            modifier = Modifier
                                .weight(1f)
                                .basicMarquee(),
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                        )
                        // Кнопка воспроизведения или паузы в зависимости от состояния воспроизведения
                        IconButton(onClick = viewModel::playOrPause) {
                            if (uiState.isPlaying) {
                                Icon(
                                    modifier = Modifier.size(34.dp),
                                    imageVector = Icons.Rounded.Pause,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    modifier = Modifier.size(34.dp),
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = null
                                )
                            }
                        }
                        // Кнопка перехода к следующему треку
                        IconButton(onClick = viewModel::playNextMusic) {
                            Icon(
                                modifier = Modifier.size(34.dp),
                                imageVector = Icons.Rounded.SkipNext,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        ) {
            // Внешнее содержимое экрана воспроизведения трека
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Расчет прозрачности для анимации скрытия панели
                val fraction = if (scaffoldState.bottomSheetState.requireOffset().isNaN()) {
                    0f
                } else {
                    scaffoldState.bottomSheetState.requireOffset() / dragRange
                }.coerceIn(0f, 1f)
                alphaValue = fraction

                // Вызов функции обратного вызова при сворачивании панели
                onSheetCollapsed(scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded)

                // Отображение контента, переданного в параметре content
                content(it)
            }
        }
    }
}