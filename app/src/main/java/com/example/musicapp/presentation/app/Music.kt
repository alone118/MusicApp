package com.example.musicapp.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.presentation.navigation.AppNavGraph
import com.example.musicapp.presentation.navigation.BottomTab
import com.example.musicapp.presentation.screens.album.albums.albumRoute
import com.example.musicapp.presentation.screens.artists.artistsRoute
import com.example.musicapp.presentation.screens.home.homeRoute
import com.example.musicapp.presentation.screens.musics.musicsRoute
import com.example.musicapp.presentation.screens.now_playing.NowPlayingSheetScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Компонент для отображения музыкального контента и навигации по приложению.
 * Этот компонент содержит основное содержимое приложения и нижнюю панель навигации.
 * @param rootChildren Список корневых элементов медиа-контента.
 * @param modifier Модификатор для настройки внешнего вида компонента.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Music(
    rootChildren: List<MediaItem>,
    modifier: Modifier = Modifier
) {
    // Создание NavController для управления навигацией в приложении
    val navController = rememberNavController()

    // Создание состояния Scaffold для управления основными действиями Scaffold
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Создание корутины для асинхронных операций
    val scope = rememberCoroutineScope()

    // Флаг, указывающий на свернутость нижнего листа Now Playing
    var nowPlayingSheetCollapsed by remember { mutableStateOf(true) }

    // Список доступных целей нижней панели навигации
    val destinations = listOf(BottomTab.Home, BottomTab.Musics, BottomTab.Albums, BottomTab.Artists)

    // Получение текущего пункта навигации из NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Список маршрутов для нижней панели навигации
    val bottomBarRoutes = listOf(homeRoute, musicsRoute, albumRoute, artistsRoute)

    // Флаг, указывающий, должна ли отображаться нижняя панель навигации
    val shouldShowBottomBar = navBackStackEntry?.destination?.route in bottomBarRoutes

    // Основной компонент Scaffold для музыкального экрана
    Scaffold(
        bottomBar = {
            // Анимированное отображение нижней панели навигации при свернутом Now Playing листе
            AnimatedVisibility(
                shouldShowBottomBar && nowPlayingSheetCollapsed,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                MusicBottomBar(
                    destinations = destinations,
                    currentDestination = currentDestination,
                    onNavigateDestination = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.startDestinationId) {
//                                saveState = true
                            }
//                            launchSingleTop = true
//                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets.navigationBars
    ) {
        // Отображение экрана Now Playing Sheet и основного контента
        NowPlayingSheetScreen(
            scaffoldState = scaffoldState,
            onSheetCollapsed = { isCollapsed -> nowPlayingSheetCollapsed = isCollapsed },
            modifier = Modifier.padding(it),
        ) { paddingValues ->
            AppNavGraph(
                rootChildren = rootChildren,
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
            BackHandler(scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                scope.launch {
                    scaffoldState.bottomSheetState.partialExpand()
                }.invokeOnCompletion {
                    nowPlayingSheetCollapsed = true
                }
            }
        }
    }
}

/**
 * Компонент для отображения нижней панели навигации.
 * @param destinations Список возможных целевых пунктов навигации.
 * @param currentDestination Текущий выбранный пункт навигации.
 * @param onNavigateDestination Функция обратного вызова для навигации к выбранному пункту.
 */

@Composable
fun MusicBottomBar(
    destinations: List<BottomTab>,
    currentDestination: NavDestination?,
    onNavigateDestination: (BottomTab) -> Unit
) {
    NavigationBar {
        // Отображение пунктов навигации в нижней панели
        destinations.forEach { destination ->
            // Определение, выбран ли текущий пункт навигации
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.route
            } ?: false
            // Отображение пункта навигации в зависимости от его выбранности
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateDestination(destination) },
                icon = {
                    if (selected) {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null
                        )
                    } else {
                        Icon(
                            imageVector = destination.unSelectedIcon,
                            contentDescription = null
                        )
                    }
                },
                label = { Text(text = destination.title) }
            )
        }
    }
}