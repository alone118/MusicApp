package com.example.musicapp.presentation.screens.now_playing.components

import android.content.Context
import android.util.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

/**
 * Данный дата класс представляет основной цвет и цвет текста для изображения.
 *
 * @property color Основной цвет изображения.
 * @property onColor Цвет текста изображения.
 */

data class DominantColor(val color: Color, val onColor: Color)

// URI ресурса для поиска изображения музыкального альбома по умолчанию.
private const val RESOURCE_URI = "android.resource://com/example.musicapp/drawable/"

/**
 * Компонент для динамической темы основных цветов, основанных на изображении.
 *
 * @param artworkData Данные изображения.
 * @param content Функция для отображения контента, зависящего от динамической темы.
 */

@Composable
fun NowPlayingDynamicTheme(
    artworkData: ByteArray?,
    content: @Composable () -> Unit
) {
    // Цвет поверхности
    val surfaceColor = MaterialTheme.colorScheme.surface
    // Состояние основного цвета
    val dominantColorState = rememberDominantColorState(
        defaultColor = MaterialTheme.colorScheme.surface
    ) { color ->
        color.contrastAgainst(surfaceColor) >= 3f
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        LaunchedEffect(artworkData) {
            // Проверяем, не пустые ли данные изображения
            if (artworkData != null) {
                if (artworkData.isNotEmpty()) {
                    // Если данные изображения не пусты, обновляем цветовую схему
                    dominantColorState.updateColorFromImage(artworkData)
                } else {
                    // Иначе сбрасываем цвета до значений по умолчанию
                    dominantColorState.reset()
                }
            } else {
                // Если данные изображения не переданы, считаем, что изображение пусто, и сбрасываем цвета до значений по умолчанию
                dominantColorState.updateColorFromImage(byteArrayOf())
            }
        }
        // Блок кода, который будет выполнен внутри [NowPlayingDynamicTheme] после обновления цветовой схемы.
        content()
    }
}

/**
 * Компонент для установки основных цветов динамической темы на основе изображения.
 *
 * @param dominantColorState Состояние для определения основных цветов.
 * @param content Функция для отображения контента, зависящего от динамической темы.
 */

@Composable
fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DominantColorState = rememberDominantColorState(),
    content: @Composable () -> Unit
) {
    // Цветовая схема
    val colorScheme = MaterialTheme.colorScheme.copy(
        primary = animateColorAsState(
            targetValue = dominantColorState.color,
            animationSpec = spring(stiffness = Spring.StiffnessLow), label = "",
        ).value,
        onPrimary = animateColorAsState(
            targetValue = dominantColorState.onColor,
            animationSpec = spring(stiffness = Spring.StiffnessLow), label = ""
        ).value
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}

/**
 * Функция для создания экземпляра [DominantColorState].
 *
 * @param context Контекст приложения.
 * @param defaultColor Цвет по умолчанию.
 * @param defaultOnColor Цвет текста по умолчанию.
 * @param cacheSize Размер кэша для цветов.
 * @param isColorValid Функция для проверки допустимости цвета.
 * @return Новый экземпляр [DominantColorState].
 */

@Composable
fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    defaultOnColor: Color = MaterialTheme.colorScheme.onPrimary,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true }
) = remember {
    DominantColorState(context, defaultColor, defaultOnColor, cacheSize, isColorValid)
}

/**
 * Класс для отслеживания основных цветов динамической темы.
 *
 * @property context Контекст приложения.
 * @property defaultColor Цвет по умолчанию.
 * @property defaultOnColor Цвет текста по умолчанию.
 * @property cache Размер кэша для цветов.
 * @property isColorValid Функция для проверки допустимости цвета.
 */

class DominantColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true }
) {
    // Основной цвет
    var color by mutableStateOf(defaultColor)
        private set

    // Цвет текста
    var onColor by mutableStateOf(defaultOnColor)
        private set

    // Кэш для хранения основных цветов изображений
    private val cache = when {
        cacheSize > 0 -> LruCache<String, DominantColor>(cacheSize)
        else -> null
    }

    /**
     * Функция для обновления основных цветов изображения.
     *
     * @param artworkData Данные изображения.
     */

    suspend fun updateColorFromImage(artworkData: ByteArray) {
        // Вычисляет доминирующий цвет на основе данных изображения альбома.
        val dominantColor = calculateDominantColor(artworkData)
        // Устанавливает цвет фона на основе доминирующего цвета изображения. Если доминирующий цвет не удалось определить,
        // устанавливается цвет по умолчанию.
        color = dominantColor?.color ?: defaultColor
        // Устанавливает цвет текста на основе доминирующего цвета изображения. Если доминирующий цвет не удалось определить,
        // устанавливается цвет по умолчанию.
        onColor = dominantColor?.onColor ?: defaultOnColor
    }

    /**
     * Функция для вычисления основного цвета изображения.
     *
     * @param artworkData Данные изображения.
     * @return Основной цвет и цвет текста изображения.
     */

    private suspend fun calculateDominantColor(
        artworkData: ByteArray
    ): DominantColor? {
        // Проверяем, есть ли закешированный доминирующий цвет для данного изображения.
        val cached = cache?.get(artworkData.toString())
        if (cached != null) return cached // Если есть, возвращаем закешированный цвет.

        // Если закешированного цвета нет, вычисляем цвета палитры изображения.
        return calculateSwatchesImage(context, artworkData)
            // Сортируем цвета по убыванию популярности.
            .sortedByDescending { swatch ->
                swatch.population
            }
            // Выбираем первый цвет из отсортированного списка, который удовлетворяет условию isColorValid.
            .firstOrNull { swatch ->
                isColorValid(Color(swatch.rgb))
            }?.let { swatch ->
                // Создаем объект DominantColor на основе выбранного цвета палитры.
                DominantColor(
                    color = Color(swatch.rgb),
                    onColor = Color(swatch.bodyTextColor)
                )
            }?.also { dominantColor ->
                // Кэшируем полученный доминирующий цвет.
                cache?.put(artworkData.toString(), dominantColor)
            }
    }

    // Функция для сброса основных цветов в значения по умолчанию.
    fun reset() {
        color = defaultColor
        onColor = defaultOnColor
    }
}

/**
 * Функция для вычисления наиболее значимых цветов на основе изображения.
 *
 * @param artworkData Данные изображения.
 * @return Список наиболее значимых цветов.
 */

private suspend fun calculateSwatchesImage(
    context: Context,
    artworkData: ByteArray
): List<Palette.Swatch> {
    // Определяем источник данных изображения.
    val data =
        if (artworkData.isNotEmpty()) artworkData else RESOURCE_URI + Icons.Rounded.MusicNote
    // Создаем запрос на загрузку изображения с помощью библиотеки Coil.
    val request = ImageRequest.Builder(context)
        .data(data)
        .size(128).scale(Scale.FILL)
        .allowHardware(false)
        .memoryCacheKey("${artworkData.toString()}.palette")
        .build()

    // Получаем изображение и конвертируем его в Bitmap.
    val bitmap = when (val result = context.imageLoader.execute(request)) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }
    // Выполняем обработку изображения для получения цветовой палитры.
    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette = Palette.Builder(it)
                .resizeBitmapArea(0)
                .clearFilters()
                .maximumColorCount(8)
                .generate()
            palette.swatches// Возвращаем список цветов палитры.
        }
    } ?: emptyList() // Возвращаем пустой список, если изображение не удалось получить.
}

/**
 * Функция для вычисления контрастности между переданным цветом и фоновым цветом.
 *
 * @param background Фоновый цвет.
 * @return Значение контрастности.
 */

fun Color.contrastAgainst(background: Color): Float {
    // Определяем передний цвет, учитывая прозрачность.
    val foreground = if (alpha < 1) compositeOver(background) else this

    // Вычисляем яркость переднего и заднего цветов с небольшим смещением.
    val foregroundLuminance = foreground.luminance() + 0.05f
    val backgroundLuminance = background.luminance() + 0.05f

    // Возвращаем коэффициент контраста между передним и задним цветами.
    return max(foregroundLuminance, backgroundLuminance) / min(
        foregroundLuminance,
        backgroundLuminance
    )
}