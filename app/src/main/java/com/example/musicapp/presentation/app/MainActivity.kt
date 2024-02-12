package com.example.musicapp.presentation.app

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musicapp.presentation.components.PermissionAction
import com.example.musicapp.presentation.components.PermissionDialog
import com.example.musicapp.presentation.theme.MusicAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Главная активность приложения, обеспечивающая запуск и инициализацию основных компонентов.
 */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Установка области содержимого окна на полный экран
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Установка контента активности
        setContent {

            // Состояние разрешения на доступ к медиа-файлам
            var isPermissionGranted by rememberSaveable { mutableStateOf(false) }

            // Применение темы приложения
            MusicAppTheme {
                val view = LocalView.current
                val darkTheme = isSystemInDarkTheme()
                // Побочный эффект для настройки цвета статус-бара
                SideEffect {
                    with(view.context as Activity){
                        window.statusBarColor = Color.Transparent.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Проверка и запрос разрешения на доступ к медиа-файлам
                    val permission =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                            Manifest.permission.READ_MEDIA_AUDIO
                        else Manifest.permission.READ_EXTERNAL_STORAGE
                    PermissionDialog(
                        context = LocalContext.current,
                        permission = permission,
                        permissionAction = { permissionAction ->
                            isPermissionGranted = when (permissionAction) {
                                PermissionAction.PermissionGranted -> true
                                PermissionAction.PermissionDenied -> false
                            }
                        }
                    )

                    // Если разрешение получено, инициализация и отображение основного контента
                    if (isPermissionGranted) {
                        val viewModel: MainActivityViewModel = viewModel()
                        val state by viewModel.uiState.collectAsState()
                        if (state.rootChildren.isNotEmpty()) {
                            Music(rootChildren = state.rootChildren)
                        }
                    }
                }
            }
        }
    }
}