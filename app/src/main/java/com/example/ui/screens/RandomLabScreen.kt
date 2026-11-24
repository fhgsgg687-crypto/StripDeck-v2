package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.ComicGenre
import com.example.model.LibraryShelfStatus
import com.example.model.MoodTag
import com.example.model.WorldComicItem
import com.example.ui.ComicUiState
import com.example.ui.components.RandomActionPillButton
import com.example.ui.theme.LocalComicColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RandomLabScreen(
    uiState: ComicUiState,
    onRollRandomWorldAny: () -> Unit,
    onRollRandomComicVine: () -> Unit,
    onRollRandomWorldBook: () -> Unit,
    onRollRandomUpcoming: () -> Unit,
    onRollRandomWorldManga: () -> Unit,
    onRollRandomPublicDomain: () -> Unit,
    onToggleSaveWorldComic: (WorldComicItem) -> Unit,
    onSaveWorldComicWithStatus: (WorldComicItem, LibraryShelfStatus) -> Unit,
    onLoadWorldComicIssues: (WorldComicItem) -> Unit,
    onClearWorldComicIssues: () -> Unit,
    onSpinRandomAny: () -> Unit,
    onSpinRandomByMood: (MoodTag) -> Unit,
    onSpinRandomByGenre: (ComicGenre) -> Unit,
    onOpenStrip: (String) -> Unit,
    onOpenSeries: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    var selectedWorldDetail by remember { mutableStateOf<WorldComicItem?>(null) }
    val savedMap = remember(uiState.preferences.savedWorldComics) {
        uiState.preferences.savedWorldComics.associateBy { it.id }
    }

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
                            text = "Roll any world comic, upcoming release, manga, or issue #",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = comicColors.sundayYellow,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${uiState.preferences.randomRollCount} ROLLS",
                            style = MaterialTheme.typography.labelMedium,
                            color = comicColors.onSundayYellow,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // World Comics Randomizer Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    RandomActionPillButton(
                        label = "SPIN RANDOM WORLD COMIC",
                        onClick = onRollRandomWorldAny,
                        icon = Icons.Filled.Public,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "lab_spin_world_btn"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Global Book",
                            onClick = onRollRandomWorldBook,
                            icon = Icons.Filled.Public,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            testTag = "lab_random_book_btn"
                        )
                        RandomActionPillButton(
                            label = "Upcoming",
                            onClick = onRollRandomUpcoming,
                            icon = Icons.Filled.Schedule,
                            containerColor = comicColors.mint,
                            contentColor = Color(0xFF06201D),
                            modifier = Modifier.weight(1f),
                            testTag = "lab_random_upcoming_btn"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Manga / Manhwa",
                            onClick = onRollRandomWorldManga,
                            icon = Icons.Filled.Casino,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            testTag = "lab_random_manga_btn"
                        )
                        RandomActionPillButton(
                            label = "Classic Archive",
                            onClick = onRollRandomPublicDomain,
                            icon = Icons.Filled.AutoStories,
                            containerColor = comicColors.sundayYellow,
                            contentColor = comicColors.onSundayYellow,
                            modifier = Modifier.weight(1f),
                            testTag = "lab_random_classic_btn"
                        )
                    }
                    RandomActionPillButton(
                        label = if (uiState.isComicVineConfigured) {
                            "Random Single Issue (Marvel • DC • Image)"
                        } else {
                            "Single Issue Picker (Add ComicVine Key)"
                        },
                        onClick = onRollRandomComicVine,
                        icon = Icons.Filled.Casino,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "lab_random_comicvine_btn"
                    )

                    if (uiState.isWorldRandomLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }

                    uiState.worldSpotlight?.let { spotlight ->
                        val isSaved = spotlight.id in savedMap
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, comicColors.sundayYellow.copy(alpha = 0.65f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedWorldDetail = spotlight }
                                .testTag("lab_world_spotlight_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    WorldCoverBox(
                                        coverUrl = spotlight.coverUrl,
                                        title = spotlight.title,
                                        width = 84.dp,
                                        height = 120.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = spotlight.source.label.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = comicColors.sundayYellow
                                        )
                                        Text(
                                            text = spotlight.title,
                                            style = MaterialTheme.typography.titleLarge,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = spotlight.creator,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            spotlight.issueLabel?.let { issue ->
                                                Surface(
                                                    color = comicColors.sundayYellow,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = issue,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = comicColors.onSundayYellow,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "Release: ${spotlight.yearOrDate}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = spotlight.synopsis,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { onToggleSaveWorldComic(spotlight) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isSaved) "In Collection" else "Add Pull List")
                                    }
                                    OutlinedButton(
                                        onClick = { selectedWorldDetail = spotlight },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CalendarMonth,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Issue Dates")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Catalog Issue # Randomizer (Metadata Only — No Free Reading Panels)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    RandomActionPillButton(
                        label = "SPIN RANDOM CATALOG ISSUE #",
                        onClick = onSpinRandomAny,
                        icon = Icons.Filled.Casino,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
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
                                border = BorderStroke(1.2.dp, comicColors.inkBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
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
                                                text = "Issue #${strip.stripNumber} • ${strip.title}",
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
                                            Text("Issue Info")
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CalendarMonth,
                                            contentDescription = null,
                                            tint = comicColors.sundayYellow,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Release Date: ${strip.publishDate}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = comicColors.sundayYellow
                                        )
                                    }

                                    Text(
                                        text = "${strip.authorNote} ${activeSeries?.synopsis ?: ""}".trim(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
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
                            text = "Random Issue by Vibe",
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
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface,
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
                        text = "Random Issue by Genre",
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

            // Recent Random Trail
            if (uiState.preferences.recentRandomIds.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Recent Random Picks",
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
                                        border = BorderStroke(1.2.dp, comicColors.inkBorder),
                                        modifier = Modifier
                                            .width(176.dp)
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
                                                text = "Issue #${strip.stripNumber} • ${strip.publishDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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

    selectedWorldDetail?.let { item ->
        LaunchedEffect(item.id) {
            onLoadWorldComicIssues(item)
        }
        WorldComicCollectorSheet(
            item = item,
            savedStatus = savedMap[item.id]?.resolveShelfStatus(),
            historyState = uiState.worldIssueHistoryState,
            onSaveWithStatus = { status -> onSaveWorldComicWithStatus(item, status) },
            onToggleSave = { onToggleSaveWorldComic(item) },
            onRollRandom = {
                onRollRandomWorldAny()
                selectedWorldDetail = null
                onClearWorldComicIssues()
            },
            onDismiss = {
                selectedWorldDetail = null
                onClearWorldComicIssues()
            }
        )
    }
}
