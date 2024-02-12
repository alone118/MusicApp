package com.example.musicapp.data.cache.util

import androidx.room.TypeConverter

/**
 * Класс-конвертер для преобразования массива байт в строку и обратно.
 *
 * Этот класс используется в Room Database для сохранения массивов байт в базе данных в виде строк
 * и обратного преобразования строк в массивы байт при извлечении данных из базы данных.
 *
 * @see TypeConverter
 *
 * @property bytes Массив байт для преобразования.
 *
 * @return Строка, представляющая массив байт.
 * @return Массив байт, представленный в виде строки.
 */

class ByteArrayConverter {
    /**
     * Преобразует массив байт в строку.
     *
     * @param bytes Массив байт для преобразования.
     * @return Строка, представляющая массив байт.
     */
    @TypeConverter
    fun toString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        return String(bytes)
    }
    /**
     * Преобразует строку в массив байт.
     *
     * @param bytes Строка, представляющая массив байт.
     * @return Массив байт, представленный в виде строки.
     */
    @TypeConverter
    fun fromString(bytes: String): ByteArray? {
        if (bytes.isEmpty()) return null
        return bytes.toByteArray()
    }
}