package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
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
        inkBorder = ComicOutlineDark,
        sundayYellow = GoldenrodYellow,
        onSundayYellow = Color(0xFF1A1100),
        vermilion = VermilionRed,
        mint = MintTeal,
        paperCard = MidnightCard
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1F6AF7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE7FF),
    onPrimaryContainer = Color(0xFF001A4D),
    secondary = VermilionRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002),
    tertiary = GoldenrodYellow,
    onTertiary = Color(0xFF2B1D00),
    tertiaryContainer = Color(0xFFFFE7A3),
    onTertiaryContainer = Color(0xFF2B1D00),
    background = NewsprintCream,
    onBackground = InkBlack,
    surface = PurePaperWhite,
    onSurface = InkBlack,
    surfaceVariant = NewsprintSurfaceVariant,
    onSurfaceVariant = InkMuted,
    outline = ComicOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = CobaltInk,
    onPrimary = Color.White,
    primaryContainer = CobaltInkContainer,
    onPrimaryContainer = OnCobaltInkContainer,
    secondary = VermilionRed,
    onSecondary = Color.White,
    secondaryContainer = VermilionContainer,
    onSecondaryContainer = OnVermilionContainer,
    tertiary = GoldenrodYellow,
    onTertiary = Color(0xFF1F1200),
    tertiaryContainer = GoldenrodContainer,
    onTertiaryContainer = OnGoldenrodContainer,
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
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val comicAccents = if (darkTheme) {
        ComicAccentColors(
            inkBorder = ComicOutlineDark,
            sundayYellow = GoldenrodYellow,
            onSundayYellow = Color(0xFF1A1100),
            vermilion = Color(0xFFFF6B6B),
            mint = Color(0xFF2DD4BF),
            paperCard = MidnightCard
        )
    } else {
        ComicAccentColors(
            inkBorder = InkBlack,
            sundayYellow = GoldenrodYellow,
            onSundayYellow = Color(0xFF2B1D00),
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
