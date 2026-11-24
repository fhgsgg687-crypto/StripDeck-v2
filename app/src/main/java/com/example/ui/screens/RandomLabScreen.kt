package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.ComicGenre
import com.example.model.MoodTag
import com.example.ui.ComicUiState
import com.example.ui.LiveXkcdUiState
import com.example.ui.components.ComicStripPanelsView
import com.example.ui.components.RandomActionPillButton
import com.example.ui.theme.InkBlack
import com.example.ui.theme.LocalComicColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RandomLabScreen(
    uiState: ComicUiState,
    onSpinRandomAny: () -> Unit,
    onSpinRandomByMood: (MoodTag) -> Unit,
    onSpinRandomByGenre: (ComicGenre) -> Unit,
    onRollRandomXkcd: () -> Unit,
    onToggleXkcdAlt: () -> Unit,
    onOpenStrip: (String) -> Unit,
    onOpenSeries: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Random Comic Lab",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "Roll any mood, genre, or live webcomic instantly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = comicColors.sundayYellow,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, InkBlack)
                    ) {
                        Text(
                            text = "${uiState.preferences.randomRollCount} ROLLS",
                            style = MaterialTheme.typography.labelMedium,
                            color = InkBlack,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Primary Big Spin Button + Live Animated Strip Deck
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RandomActionPillButton(
                        label = "SPIN RANDOM COMIC STRIP",
                        onClick = onSpinRandomAny,
                        icon = Icons.Filled.Casino,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "lab_spin_random_btn"
                    )

                    uiState.labActiveStrip?.let { activeStrip ->
                        val activeSeries = uiState.allSeries.find { it.id == activeStrip.seriesId }
                        AnimatedContent(
                            targetState = activeStrip,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "lab_strip_transition"
                        ) { strip ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(2.dp, InkBlack),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    activeSeries?.let { onOpenSeries(it.id) }
                                                }
                                        ) {
                                            Text(
                                                text = activeSeries?.title ?: "Comic",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "#${strip.stripNumber} • ${strip.title}",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        OutlinedButton(
                                            onClick = { onOpenStrip(strip.id) },
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .testTag("lab_open_reader_btn"),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.OpenInFull,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Reader")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    ComicStripPanelsView(strip = strip, compactMode = true)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "${strip.publishDate} — ${strip.authorNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Random by Mood Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = comicColors.vermilion,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Random by Mood & Vibe",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MoodTag.entries.forEach { mood ->
                            RandomActionPillButton(
                                label = mood.label,
                                onClick = { onSpinRandomByMood(mood) },
                                icon = Icons.Filled.Casino,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                testTag = "mood_random_${mood.name.lowercase()}"
                            )
                        }
                    }
                }
            }

            // Random by Genre Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Random by Category",
                        style = MaterialTheme.typography.titleLarge
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComicGenre.entries.forEach { genre ->
                            OutlinedButton(
                                onClick = { onSpinRandomByGenre(genre) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("genre_random_${genre.name.lowercase()}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(genre.label)
                            }
                        }
                    }
                }
            }

            // Live Webcomic XKCD Randomizer Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.8.dp, InkBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Public,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Live Webcomic: XKCD",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "Fetched live from xkcd.com archive",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            RandomActionPillButton(
                                label = stringResource(R.string.btn_live_xkcd),
                                onClick = onRollRandomXkcd,
                                icon = Icons.Filled.Refresh,
                                containerColor = InkBlack,
                                contentColor = Color.White,
                                testTag = "live_xkcd_random_btn"
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (val xkcd = uiState.liveXkcdState) {
                            is LiveXkcdUiState.Idle, LiveXkcdUiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is LiveXkcdUiState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Could not load live XKCD right now",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "Offline mode active — all ${uiState.allSeries.size} curated comic series above work offline. Tap retry when connected.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            is LiveXkcdUiState.Success -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "#${xkcd.comic.number} — ${xkcd.comic.title} (${xkcd.comic.publishDate})",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Surface(
                                        color = Color.White,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, InkBlack.copy(alpha = 0.25f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        AsyncImage(
                                            model = xkcd.comic.imageUrl,
                                            contentDescription = xkcd.comic.altText,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .height(220.dp)
                                        )
                                    }
                                    TextButton(onClick = onToggleXkcdAlt) {
                                        Text(
                                            if (uiState.showXkcdAltText) "Hide Hover Alt-Text"
                                            else "Show Secret Alt-Text"
                                        )
                                    }
                                    if (uiState.showXkcdAltText) {
                                        Text(
                                            text = xkcd.comic.altText,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Recent Random Trail
            if (uiState.preferences.recentRandomIds.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Recent Random Trail",
                            style = MaterialTheme.typography.titleLarge
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(uiState.preferences.recentRandomIds) { stripId ->
                                val strip = uiState.allSeries.flatMap { it.strips }.find { it.id == stripId }
                                val series = strip?.let { s -> uiState.allSeries.find { it.id == s.seriesId } }
                                if (strip != null) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.2.dp, comicColors.inkBorder.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .width(170.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .clickable { onOpenStrip(strip.id) }
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = series?.title ?: "Comic",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = strip.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "#${strip.stripNumber}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}
