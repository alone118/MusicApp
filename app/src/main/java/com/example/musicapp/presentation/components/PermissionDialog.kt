package com.example.musicapp.presentation.components

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.musicapp.R
import com.example.musicapp.presentation.extensions.SpacerHeight
import com.example.musicapp.presentation.theme.MediumSpacing
import com.example.musicapp.presentation.theme.yellow

/**
 * Компонент для отображения диалога запроса разрешений.
 *
 * @param context Контекст приложения.
 * @param permission Название запрашиваемого разрешения.
 * @param permissionAction Функция обратного вызова, вызываемая после получения ответа на запрос разрешений.
 * @param modifier Модификатор, применяемый к компоненту.
 */

sealed interface PermissionAction {
    data object PermissionGranted : PermissionAction

    data object PermissionDenied : PermissionAction
}

@Composable
fun PermissionDialog(
    context: Context,
    permission: String,
    permissionAction: (PermissionAction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Проверка, было ли предоставлено разрешение ранее.
    if (ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Если разрешение уже предоставлено, вызываем обратный вызов с типом PermissionGranted.
        permissionAction(PermissionAction.PermissionGranted)
        return
    }

    // Получение лаунчера для запроса разрешения.
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Обработка результата запроса разрешения.
            if (isGranted) {
                permissionAction(PermissionAction.PermissionGranted)
            } else {
                permissionAction(PermissionAction.PermissionDenied)
            }
        }

    // Проверка, необходимо ли показывать пользователю объяснение, зачем нужно данное разрешение.
    val activity = context as Activity
    val shouldShowPermissionRationale = activity.shouldShowRequestPermissionRationale(permission)

    // Отображение диалога запроса разрешения.
    if (shouldShowPermissionRationale) {
        Column(
            modifier = modifier.padding(MediumSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            // Изображение, символизирующее запрос разрешения.
            Image(
                modifier = Modifier.size(192.dp),
                imageVector = Icons.Rounded.Folder,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(color = yellow)
            )

            // Текст объяснения необходимости разрешения.
            Text(
                text = stringResource(id = R.string.dialog),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Thin,
            )
            SpacerHeight(MediumSpacing)
            // Кнопка для предоставления разрешения.
            Button(onClick = {
                permissionLauncher.launch(permission)
            }) {
                Text(text = stringResource(id = R.string.grant))
            }
            Spacer(modifier = Modifier.weight(4f))
        }
    } else {
        // Если объяснение не нужно, сразу запрашиваем разрешение.
        SideEffect {
            permissionLauncher.launch(permission)
        }
    }
}