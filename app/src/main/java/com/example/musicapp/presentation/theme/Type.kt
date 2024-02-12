package com.example.data.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

val INTER_FONT = FontFamily(Font(R.font.inter))

val HK_GROTESK_FONTS = FontFamily(
    Font(R.font.hk_grotesk_regular, FontWeight.Normal),
    Font(R.font.hk_grotesk_bold, FontWeight.Bold),
    Font(R.font.hk_grotesk_semi_bold, FontWeight.SemiBold),
    Font(R.font.hk_grotesk_light, FontWeight.Light),
    Font(R.font.hk_grotesk_medium, FontWeight.Medium),
    Font(R.font.hk_grotesk_italic, FontWeight.Thin),
)

// Set of Material typography styles to start with
val Typography = Typography(
    headlineSmall = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    titleSmall = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = HK_GROTESK_FONTS,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
)