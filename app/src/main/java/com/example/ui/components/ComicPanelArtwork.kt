package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CharacterArchetype
import com.example.model.ComicStrip
import com.example.model.StripPanel
import com.example.ui.theme.InkBlack
import com.example.ui.theme.LocalComicColors

@Composable
fun ComicStripPanelsView(
    strip: ComicStrip,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compactMode) 8.dp else 12.dp)
    ) {
        strip.panels.forEachIndexed { index, panel ->
            ComicSinglePanelCard(
                panel = panel,
                panelNumber = index + 1,
                panelHeight = if (compactMode) 176.dp else 224.dp
            )
        }
    }
}

@Composable
fun ComicSinglePanelCard(
    panel: StripPanel,
    panelNumber: Int,
    modifier: Modifier = Modifier,
    panelHeight: Dp = 210.dp
) {
    val comicColors = LocalComicColors.current
    val panelBg = Color(panel.bgHex)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(panelHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(panelBg)
            .border(2.dp, InkBlack, RoundedCornerShape(12.dp))
    ) {
        // Scene + Characters Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawSceneBackdrop(scene = panel.scene)
            drawHalftoneCorner(color = InkBlack.copy(alpha = 0.08f))

            val primaryCenter = if (panel.secondaryArchetype != null) {
                Offset(size.width * 0.30f, size.height * 0.72f)
            } else {
                Offset(size.width * 0.28f, size.height * 0.72f)
            }

            drawCharacterIllustration(
                archetype = panel.archetype,
                pose = panel.pose,
                center = primaryCenter,
                scale = (size.height / 210f).coerceIn(0.75f, 1.25f)
            )

            panel.secondaryArchetype?.let { second ->
                drawCharacterIllustration(
                    archetype = second,
                    pose = "happy",
                    center = Offset(size.width * 0.78f, size.height * 0.74f),
                    scale = (size.height / 245f).coerceIn(0.65f, 1.05f)
                )
            }
        }

        // Top Narration Caption Box
        if (!panel.caption.isNullOrBlank()) {
            Surface(
                color = comicColors.sundayYellow,
                shape = RoundedCornerShape(bottomEnd = 8.dp),
                border = BorderStroke(1.5.dp, InkBlack),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = panel.caption.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    ),
                    color = InkBlack,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        } else {
            // Subtle panel index badge
            Surface(
                color = Color.White.copy(alpha = 0.85f),
                shape = RoundedCornerShape(bottomEnd = 8.dp),
                border = BorderStroke(1.dp, InkBlack.copy(alpha = 0.45f)),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "#$panelNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = InkBlack,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Comic SFX Starburst Badge
        if (!panel.sfx.isNullOrBlank()) {
            Surface(
                color = comicColors.vermilion,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.5.dp, InkBlack),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .rotate(-6f)
            ) {
                Text(
                    text = panel.sfx,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Speech or Thought Bubble
        val bubbleText = panel.dialogue ?: panel.thought
        val isThought = panel.dialogue == null && panel.thought != null
        if (!bubbleText.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = if (!panel.caption.isNullOrBlank()) 28.dp else 12.dp,
                        end = 12.dp,
                        start = 108.dp
                    )
            ) {
                Surface(
                    color = Color.White,
                    shape = if (isThought) RoundedCornerShape(22.dp) else RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        width = 1.8.dp,
                        color = if (isThought) InkBlack.copy(alpha = 0.65f) else InkBlack
                    ),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
                        if (!panel.speaker.isNullOrBlank()) {
                            Text(
                                text = panel.speaker,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = if (isThought) "($bubbleText)" else bubbleText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = if (isThought) FontStyle.Italic else FontStyle.Normal,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 17.sp
                            ),
                            color = InkBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterAvatarBadge(
    archetype: CharacterArchetype,
    accentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.18f))
            .border(2.dp, InkBlack, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCharacterIllustration(
                archetype = archetype,
                pose = "happy",
                center = Offset(this.size.width / 2f, this.size.height * 0.60f),
                scale = (this.size.height / 92f)
            )
        }
    }
}

private fun DrawScope.drawHalftoneCorner(color: Color) {
    val step = 10.dp.toPx()
    for (row in 0..4) {
        for (col in 0..5) {
            val radius = (3.2f - row * 0.45f).coerceAtLeast(1f)
            drawCircle(
                color = color,
                radius = radius,
                center = Offset(size.width - 14.dp.toPx() - col * step, 14.dp.toPx() + row * step)
            )
        }
    }
}

private fun DrawScope.drawSceneBackdrop(scene: String) {
    val groundY = size.height * 0.80f
    when (scene) {
        "space" -> {
            drawCircle(
                color = Color(0xFF1F6AF7).copy(alpha = 0.14f),
                radius = size.height * 0.24f,
                center = Offset(size.width * 0.82f, size.height * 0.25f)
            )
            drawOval(
                color = Color(0xFFFFB300).copy(alpha = 0.35f),
                topLeft = Offset(size.width * 0.68f, size.height * 0.21f),
                size = Size(size.height * 0.50f, size.height * 0.12f),
                style = Stroke(width = 3.dp.toPx())
            )
            // Stars
            val starCoords = listOf(0.14f to 0.22f, 0.45f to 0.15f, 0.62f to 0.44f, 0.90f to 0.56f)
            starCoords.forEach { (xr, yr) ->
                drawCircle(
                    color = Color(0xFF1F6AF7).copy(alpha = 0.35f),
                    radius = 2.5.dp.toPx(),
                    center = Offset(size.width * xr, size.height * yr)
                )
            }
            // Asteroid floor
            drawRect(
                color = Color(0xFF8C9BB8).copy(alpha = 0.30f),
                topLeft = Offset(0f, groundY),
                size = Size(size.width, size.height - groundY)
            )
        }
        "kitchen", "office" -> {
            // Floor / Counter line
            drawRect(
                color = Color(0xFFDFCFB6).copy(alpha = 0.55f),
                topLeft = Offset(0f, groundY),
                size = Size(size.width, size.height - groundY)
            )
            drawLine(
                color = InkBlack.copy(alpha = 0.35f),
                start = Offset(0f, groundY),
                end = Offset(size.width, groundY),
                strokeWidth = 2.dp.toPx()
            )
            // Coffee mug prop on right
            val mugX = size.width * 0.86f
            val mugY = groundY - 18.dp.toPx()
            drawRoundRect(
                color = Color(0xFFE53935),
                topLeft = Offset(mugX, mugY),
                size = Size(16.dp.toPx(), 18.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx())
            )
            drawRoundRect(
                color = InkBlack,
                topLeft = Offset(mugX, mugY),
                size = Size(16.dp.toPx(), 18.dp.toPx()),
                cornerRadius = CornerRadius(3.dp.toPx()),
                style = Stroke(width = 1.6.dp.toPx())
            )
        }
        "park", "forest" -> {
            drawRect(
                color = Color(0xFF81C784).copy(alpha = 0.38f),
                topLeft = Offset(0f, groundY),
                size = Size(size.width, size.height - groundY)
            )
            drawLine(
                color = InkBlack.copy(alpha = 0.35f),
                start = Offset(0f, groundY),
                end = Offset(size.width, groundY),
                strokeWidth = 2.dp.toPx()
            )
            // Soft cloud/bush silhouette
            drawCircle(
                color = Color(0xFF4CAF50).copy(alpha = 0.18f),
                radius = 28.dp.toPx(),
                center = Offset(size.width * 0.88f, groundY - 8.dp.toPx())
            )
        }
        else -> {
            drawRect(
                color = Color(0xFFE3D9C6).copy(alpha = 0.50f),
                topLeft = Offset(0f, groundY),
                size = Size(size.width, size.height - groundY)
            )
            drawLine(
                color = InkBlack.copy(alpha = 0.30f),
                start = Offset(0f, groundY),
                end = Offset(size.width, groundY),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

private fun DrawScope.drawCharacterIllustration(
    archetype: CharacterArchetype,
    pose: String,
    center: Offset,
    scale: Float
) {
    val s = scale * density
    val strokeW = 2.2f * s
    val headR = 22f * s
    val bodyColor = when (archetype) {
        CharacterArchetype.ORANGE_CAT -> Color(0xFFFF9800)
        CharacterArchetype.TABBY_CAT -> Color(0xFFD9822B)
        CharacterArchetype.KID_EXPLORER -> Color(0xFFE53935)
        CharacterArchetype.BOXY_ROBOT -> Color(0xFF26A69A)
        CharacterArchetype.POTTED_CACTUS -> Color(0xFF43A047)
        CharacterArchetype.TIRED_DEV -> Color(0xFF3F51B5)
        CharacterArchetype.DOG_DETECTIVE -> Color(0xFFA1887F)
        CharacterArchetype.WISE_OWL -> Color(0xFF5C6BC0)
        CharacterArchetype.GRANNY_PUNK -> Color(0xFFEC407A)
        CharacterArchetype.ALIEN_GUEST -> Color(0xFF66BB6A)
        CharacterArchetype.MINIMAL_DUCK -> Color(0xFFFFFFFF)
        CharacterArchetype.SPACE_PILOT -> Color(0xFF1E88E5)
        CharacterArchetype.STICK_GENIUS -> Color(0xFFFFB300)
    }

    // Shoulders / Body
    val bodyTop = center.y + headR * 0.55f
    if (archetype == CharacterArchetype.POTTED_CACTUS) {
        // Terracotta pot
        val potPath = Path().apply {
            moveTo(center.x - 18f * s, bodyTop - 2f * s)
            lineTo(center.x + 18f * s, bodyTop - 2f * s)
            lineTo(center.x + 13f * s, bodyTop + 22f * s)
            lineTo(center.x - 13f * s, bodyTop + 22f * s)
            close()
        }
        drawPath(potPath, color = Color(0xFFD86C44))
        drawPath(potPath, color = InkBlack, style = Stroke(width = strokeW))
    } else {
        drawRoundRect(
            color = bodyColor,
            topLeft = Offset(center.x - 17f * s, bodyTop),
            size = Size(34f * s, 26f * s),
            cornerRadius = CornerRadius(10f * s)
        )
        drawRoundRect(
            color = InkBlack,
            topLeft = Offset(center.x - 17f * s, bodyTop),
            size = Size(34f * s, 26f * s),
            cornerRadius = CornerRadius(10f * s),
            style = Stroke(width = strokeW)
        )
    }

    // Ears / Accessories behind or above head
    when (archetype) {
        CharacterArchetype.ORANGE_CAT, CharacterArchetype.TABBY_CAT -> {
            val leftEar = Path().apply {
                moveTo(center.x - 18f * s, center.y - 10f * s)
                lineTo(center.x - 22f * s, center.y - 32f * s)
                lineTo(center.x - 4f * s, center.y - 19f * s)
                close()
            }
            val rightEar = Path().apply {
                moveTo(center.x + 18f * s, center.y - 10f * s)
                lineTo(center.x + 22f * s, center.y - 32f * s)
                lineTo(center.x + 4f * s, center.y - 19f * s)
                close()
            }
            drawPath(leftEar, color = bodyColor)
            drawPath(leftEar, color = InkBlack, style = Stroke(width = strokeW))
            drawPath(rightEar, color = bodyColor)
            drawPath(rightEar, color = InkBlack, style = Stroke(width = strokeW))
        }
        CharacterArchetype.BOXY_ROBOT -> {
            drawLine(
                color = InkBlack,
                start = Offset(center.x, center.y - headR),
                end = Offset(center.x, center.y - headR - 12f * s),
                strokeWidth = strokeW
            )
            drawCircle(
                color = Color(0xFFFFB300),
                radius = 4f * s,
                center = Offset(center.x, center.y - headR - 13f * s)
            )
        }
        CharacterArchetype.ALIEN_GUEST -> {
            drawLine(
                color = InkBlack,
                start = Offset(center.x - 8f * s, center.y - headR),
                end = Offset(center.x - 13f * s, center.y - headR - 11f * s),
                strokeWidth = strokeW
            )
            drawLine(
                color = InkBlack,
                start = Offset(center.x + 8f * s, center.y - headR),
                end = Offset(center.x + 13f * s, center.y - headR - 11f * s),
                strokeWidth = strokeW
            )
            drawCircle(Color(0xFFFFB300), 3.5f * s, Offset(center.x - 13f * s, center.y - headR - 12f * s))
            drawCircle(Color(0xFFFFB300), 3.5f * s, Offset(center.x + 13f * s, center.y - headR - 12f * s))
        }
        CharacterArchetype.DOG_DETECTIVE -> {
            // Floppy ears
            drawOval(
                color = Color(0xFF6D4C41),
                topLeft = Offset(center.x - 27f * s, center.y - 10f * s),
                size = Size(11f * s, 24f * s)
            )
            drawOval(
                color = Color(0xFF6D4C41),
                topLeft = Offset(center.x + 16f * s, center.y - 10f * s),
                size = Size(11f * s, 24f * s)
            )
        }
        else -> {}
    }

    // Head shape
    val headFill = when (archetype) {
        CharacterArchetype.KID_EXPLORER, CharacterArchetype.TIRED_DEV,
        CharacterArchetype.GRANNY_PUNK, CharacterArchetype.SPACE_PILOT -> Color(0xFFFFDFC4)
        CharacterArchetype.MINIMAL_DUCK -> Color(0xFFFFFFFF)
        CharacterArchetype.STICK_GENIUS -> Color(0xFFFFE082)
        else -> bodyColor
    }

    if (archetype == CharacterArchetype.BOXY_ROBOT) {
        drawRoundRect(
            color = Color(0xFFE0F2F1),
            topLeft = Offset(center.x - headR, center.y - headR),
            size = Size(headR * 2f, headR * 1.85f),
            cornerRadius = CornerRadius(8f * s)
        )
        drawRoundRect(
            color = InkBlack,
            topLeft = Offset(center.x - headR, center.y - headR),
            size = Size(headR * 2f, headR * 1.85f),
            cornerRadius = CornerRadius(8f * s),
            style = Stroke(width = strokeW)
        )
    } else if (archetype == CharacterArchetype.POTTED_CACTUS) {
        drawRoundRect(
            color = Color(0xFF43A047),
            topLeft = Offset(center.x - 15f * s, center.y - 26f * s),
            size = Size(30f * s, 42f * s),
            cornerRadius = CornerRadius(15f * s)
        )
        drawRoundRect(
            color = InkBlack,
            topLeft = Offset(center.x - 15f * s, center.y - 26f * s),
            size = Size(30f * s, 42f * s),
            cornerRadius = CornerRadius(15f * s),
            style = Stroke(width = strokeW)
        )
    } else {
        drawCircle(
            color = headFill,
            radius = headR,
            center = center
        )
        drawCircle(
            color = InkBlack,
            radius = headR,
            center = center,
            style = Stroke(width = strokeW)
        )
    }

    // Granny bun / Space helmet / Kid hair accent
    when (archetype) {
        CharacterArchetype.GRANNY_PUNK -> {
            drawCircle(Color(0xFFE8EAF6), 10f * s, Offset(center.x, center.y - headR - 3f * s))
            drawCircle(InkBlack, 10f * s, Offset(center.x, center.y - headR - 3f * s), style = Stroke(strokeW))
        }
        CharacterArchetype.SPACE_PILOT -> {
            drawArc(
                color = Color(0xFF90CAF9).copy(alpha = 0.45f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - 26f * s, center.y - 26f * s),
                size = Size(52f * s, 52f * s),
                style = Stroke(width = 3f * s)
            )
        }
        CharacterArchetype.MINIMAL_DUCK -> {
            // Orange duck bill
            drawOval(
                color = Color(0xFFFF9800),
                topLeft = Offset(center.x + 2f * s, center.y + 1f * s),
                size = Size(16f * s, 8f * s)
            )
            drawOval(
                color = InkBlack,
                topLeft = Offset(center.x + 2f * s, center.y + 1f * s),
                size = Size(16f * s, 8f * s),
                style = Stroke(width = 1.6f * s)
            )
        }
        else -> {}
    }

    // Eyes based on pose
    val eyeY = center.y - 4f * s
    when (pose) {
        "sleepy" -> {
            drawLine(InkBlack, Offset(center.x - 11f * s, eyeY), Offset(center.x - 4f * s, eyeY), strokeW, StrokeCap.Round)
            drawLine(InkBlack, Offset(center.x + 4f * s, eyeY), Offset(center.x + 11f * s, eyeY), strokeW, StrokeCap.Round)
        }
        "smug" -> {
            drawLine(InkBlack, Offset(center.x - 11f * s, eyeY - 2f * s), Offset(center.x - 4f * s, eyeY), strokeW, StrokeCap.Round)
            drawCircle(InkBlack, 2.8f * s, Offset(center.x - 7f * s, eyeY + 2f * s))
            drawLine(InkBlack, Offset(center.x + 4f * s, eyeY), Offset(center.x + 11f * s, eyeY - 2f * s), strokeW, StrokeCap.Round)
            drawCircle(InkBlack, 2.8f * s, Offset(center.x + 7f * s, eyeY + 2f * s))
        }
        "shocked" -> {
            drawCircle(Color.White, 5f * s, Offset(center.x - 8f * s, eyeY))
            drawCircle(InkBlack, 5f * s, Offset(center.x - 8f * s, eyeY), style = Stroke(1.6f * s))
            drawCircle(InkBlack, 2f * s, Offset(center.x - 8f * s, eyeY))
            drawCircle(Color.White, 5f * s, Offset(center.x + 8f * s, eyeY))
            drawCircle(InkBlack, 5f * s, Offset(center.x + 8f * s, eyeY), style = Stroke(1.6f * s))
            drawCircle(InkBlack, 2f * s, Offset(center.x + 8f * s, eyeY))
        }
        else -> {
            drawCircle(InkBlack, 2.8f * s, Offset(center.x - 8f * s, eyeY))
            drawCircle(InkBlack, 2.8f * s, Offset(center.x + 8f * s, eyeY))
        }
    }

    // Mouth based on pose (skip if duck since duck has bill)
    if (archetype != CharacterArchetype.MINIMAL_DUCK) {
        val mouthY = center.y + 8f * s
        when (pose) {
            "shocked" -> drawCircle(InkBlack, 4f * s, Offset(center.x, mouthY + 2f * s))
            "sleepy" -> drawLine(InkBlack, Offset(center.x - 5f * s, mouthY), Offset(center.x + 5f * s, mouthY), strokeW)
            else -> {
                drawArc(
                    color = InkBlack,
                    startAngle = 15f,
                    sweepAngle = 150f,
                    useCenter = false,
                    topLeft = Offset(center.x - 7f * s, mouthY - 4f * s),
                    size = Size(14f * s, 9f * s),
                    style = Stroke(width = strokeW, cap = StrokeCap.Round)
                )
            }
        }
    }
}
