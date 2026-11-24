package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Immutable
data class ComicAccentColors(
    val inkBorder: Color,
    val sundayYellow: Color,
    val onSundayYellow: Color,
    val vermilion: Color,
    val mint: Color,
    val paperCard: Color
)

val LocalComicColors = staticCompositionLocalOf {
    ComicAccentColors(
        inkBorder = InkBlack,
        sundayYellow = GoldenrodYellow,
        onSundayYellow = OnGoldenrodContainer,
        vermilion = VermilionRed,
        mint = MintTeal,
        paperCard = PurePaperWhite
    )
}

private val LightColorScheme = lightColorScheme(
    primary = CobaltInk,
    onPrimary = Color.White,
    primaryContainer = CobaltInkContainer,
    onPrimaryContainer = OnCobaltInkContainer,
    secondary = VermilionRed,
    onSecondary = Color.White,
    secondaryContainer = VermilionContainer,
    onSecondaryContainer = OnVermilionContainer,
    tertiary = GoldenrodYellow,
    onTertiary = OnGoldenrodContainer,
    tertiaryContainer = GoldenrodContainer,
    onTertiaryContainer = OnGoldenrodContainer,
    background = NewsprintCream,
    onBackground = InkBlack,
    surface = PurePaperWhite,
    onSurface = InkBlack,
    surfaceVariant = NewsprintSurfaceVariant,
    onSurfaceVariant = InkMuted,
    outline = ComicOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7CA7FF),
    onPrimary = Color(0xFF002D75),
    primaryContainer = Color(0xFF14429E),
    onPrimaryContainer = Color(0xFFDCE7FF),
    secondary = Color(0xFFFF8A80),
    onSecondary = Color(0xFF5F0A0A),
    secondaryContainer = Color(0xFF8C1D18),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color(0xFF3E2B00),
    tertiaryContainer = Color(0xFF5D4200),
    onTertiaryContainer = Color(0xFFFFE7A3),
    background = MidnightInk,
    onBackground = PaperCreamText,
    surface = MidnightCard,
    onSurface = PaperCreamText,
    surfaceVariant = MidnightElevated,
    onSurfaceVariant = PaperMutedText,
    outline = ComicOutlineDark
)

val StripDeckShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun StripDeckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val comicAccents = if (darkTheme) {
        ComicAccentColors(
            inkBorder = Color(0xFF495063),
            sundayYellow = Color(0xFFFFCA28),
            onSundayYellow = Color(0xFF281B00),
            vermilion = Color(0xFFFF6E69),
            mint = Color(0xFF4DB6AC),
            paperCard = MidnightCard
        )
    } else {
        ComicAccentColors(
            inkBorder = InkBlack,
            sundayYellow = GoldenrodYellow,
            onSundayYellow = OnGoldenrodContainer,
            vermilion = VermilionRed,
            mint = MintTeal,
            paperCard = PurePaperWhite
        )
    }

    CompositionLocalProvider(LocalComicColors provides comicAccents) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = StripDeckTypography,
            shapes = StripDeckShapes,
            content = content
        )
    }
}
