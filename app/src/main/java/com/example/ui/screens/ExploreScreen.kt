package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ComicGenre
import com.example.model.ExploreSortOption
import com.example.model.IssueTimelineEntry
import com.example.model.LibraryShelfStatus
import com.example.model.WorldComicItem
import com.example.ui.ComicUiState
import com.example.ui.components.ComicSeriesCard
import com.example.ui.components.RandomActionPillButton
import com.example.ui.components.StripListPreviewCard
import com.example.ui.theme.LocalComicColors

@Composable
fun ExploreScreen(
    uiState: ComicUiState,
    onQueryChange: (String) -> Unit,
    onSelectGenre: (ComicGenre?) -> Unit,
    onSelectSort: (ExploreSortOption) -> Unit,
    onClearFilters: () -> Unit,
    onPickRandomMatch: () -> Unit,
    onOpenSeries: (String) -> Unit,
    onOpenStrip: (String) -> Unit,
    onToggleFavoriteSeries: (String) -> Unit,
    onToggleBookmarkStrip: (String) -> Unit,
    onToggleSaveWorldComic: (WorldComicItem) -> Unit,
    onSaveWorldComicWithStatus: (WorldComicItem, LibraryShelfStatus) -> Unit,
    onLoadWorldComicIssues: (WorldComicItem) -> Unit,
    onClearWorldComicIssues: () -> Unit,
    onRollRandomComicVine: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showIssueTimeline by remember { mutableStateOf(true) }
    var releaseFilterMode by remember { mutableStateOf(0) } // 0 = All, 1 = Upcoming, 2 = Released
    var selectedWorldIssue by remember { mutableStateOf<WorldComicItem?>(null) }
    val savedWorldMap = remember(uiState.preferences.savedWorldComics) {
        uiState.preferences.savedWorldComics.associateBy { it.id }
    }

    val seriesGenreMap = remember(uiState.allSeries) {
        uiState.allSeries.associate { it.id to it.genre }
    }
    val q = uiState.searchQuery.trim().lowercase().removePrefix("#")
    val filteredTimeline = remember(
        uiState.issueCalendar,
        uiState.searchQuery,
        uiState.selectedGenre,
        releaseFilterMode
    ) {
        uiState.issueCalendar.filter { entry ->
            val matchesMode = when (releaseFilterMode) {
                1 -> entry.isUpcoming
                2 -> !entry.isUpcoming
                else -> true
            }
            val matchesGenre = uiState.selectedGenre == null ||
                seriesGenreMap[entry.seriesId] == uiState.selectedGenre
            val matchesQ = q.isEmpty() ||
                entry.issueNumber.toString().contains(q) ||
                entry.releaseDate.lowercase().contains(q) ||
                entry.seriesTitle.lowercase().contains(q) ||
                entry.title.lowercase().contains(q)
            matchesMode && matchesGenre && matchesQ
        }
    }
    val filteredLiveCalendar = remember(uiState.liveCalendarIssues, uiState.searchQuery) {
        uiState.liveCalendarIssues.filter { item ->
            q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.creator.lowercase().contains(q) ||
                item.yearOrDate.lowercase().contains(q) ||
                (item.issueLabel?.lowercase()?.contains(q) == true)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Release Calendar",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = if (showIssueTimeline) {
                            "${filteredLiveCalendar.size + filteredTimeline.size} issues by release date"
                        } else {
                            "${uiState.filteredSeries.size} series matching"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RandomActionPillButton(
                    label = "Random Match",
                    onClick = onPickRandomMatch,
                    icon = Icons.Filled.Casino,
                    testTag = "explore_random_match_btn"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mode Toggle: Issues & Dates (Default) vs Series Directory
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = showIssueTimeline,
                    onClick = { showIssueTimeline = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Issues & Dates (${filteredTimeline.size})") },
                    modifier = Modifier.testTag("explore_mode_issues")
                )
                FilterChip(
                    selected = !showIssueTimeline,
                    onClick = { showIssueTimeline = false },
                    label = { Text("Series (${uiState.filteredSeries.size})") },
                    modifier = Modifier.testTag("explore_mode_series")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        if (showIssueTimeline) {
                            "Search by #issue, date (Nov 18), or comic..."
                        } else {
                            stringResource(R.string.search_placeholder)
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (showIssueTimeline) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = releaseFilterMode == 0,
                        onClick = { releaseFilterMode = 0 },
                        label = { Text("All Dates") }
                    )
                    FilterChip(
                        selected = releaseFilterMode == 1,
                        onClick = { releaseFilterMode = 1 },
                        label = { Text("Upcoming") }
                    )
                    FilterChip(
                        selected = releaseFilterMode == 2,
                        onClick = { releaseFilterMode = 2 },
                        label = { Text("Released") }
                    )
                }
            }

            // Genre Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.selectedGenre == null,
                        onClick = { onSelectGenre(null) },
                        label = { Text("All Genres") },
                        modifier = Modifier.testTag("genre_chip_all")
                    )
                }
                items(ComicGenre.entries) { genre ->
                    FilterChip(
                        selected = uiState.selectedGenre == genre,
                        onClick = {
                            onSelectGenre(if (uiState.selectedGenre == genre) null else genre)
                        },
                        label = { Text(genre.label) },
                        modifier = Modifier.testTag("genre_chip_${genre.name.lowercase()}")
                    )
                }
            }

            if (!showIssueTimeline) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ExploreSortOption.entries) { sort ->
                        FilterChip(
                            selected = uiState.sortOption == sort,
                            onClick = { onSelectSort(sort) },
                            label = { Text(sort.label) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (showIssueTimeline) {
                if (uiState.isCalendarLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                }
                if (filteredLiveCalendar.isEmpty() && filteredTimeline.isEmpty()) {
                    EmptyDirectoryState(
                        query = uiState.searchQuery,
                        onClearFilters = onClearFilters
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        if (filteredLiveCalendar.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Live Single-Issue Releases (${filteredLiveCalendar.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            items(filteredLiveCalendar, key = { "exp_cv_${it.id}" }) { item ->
                                WorldComicCard(
                                    item = item,
                                    savedStatus = savedWorldMap[item.id]?.resolveShelfStatus(),
                                    onToggleSave = { onToggleSaveWorldComic(item) },
                                    onClick = { selectedWorldIssue = item }
                                )
                            }
                        }
                        if (filteredTimeline.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Curated Issue Schedule (${filteredTimeline.size})",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            items(filteredTimeline, key = { "exp_tl_${it.id}" }) { entry ->
                                ExploreIssueTimelineCard(
                                    entry = entry,
                                    onClick = {
                                        if (entry.stripId != null) {
                                            onOpenStrip(entry.stripId)
                                        } else {
                                            onOpenSeries(entry.seriesId)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else if (uiState.filteredSeries.isEmpty() && uiState.filteredStrips.isEmpty()) {
                EmptyDirectoryState(
                    query = uiState.searchQuery,
                    onClearFilters = onClearFilters
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(uiState.filteredSeries, key = { it.id }) { series ->
                        ComicSeriesCard(
                            series = series,
                            isFavorite = series.id in uiState.preferences.favoriteSeriesIds,
                            onClick = { onOpenSeries(series.id) },
                            onToggleFavorite = { onToggleFavoriteSeries(series.id) }
                        )
                    }

                    if (uiState.filteredStrips.isNotEmpty() && uiState.searchQuery.isNotBlank()) {
                        item {
                            Text(
                                text = "Matching Issues (${uiState.filteredStrips.size})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(uiState.filteredStrips.take(8), key = { "exp_strip_${it.id}" }) { strip ->
                            val series = uiState.allSeries.find { it.id == strip.seriesId }
                            StripListPreviewCard(
                                strip = strip,
                                series = series,
                                isBookmarked = strip.id in uiState.preferences.bookmarkedStripIds,
                                onReadClick = { onOpenStrip(strip.id) },
                                onToggleBookmark = { onToggleBookmarkStrip(strip.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedWorldIssue?.let { item ->
        LaunchedEffect(item.id) {
            onLoadWorldComicIssues(item)
        }
        WorldComicCollectorSheet(
            item = item,
            savedStatus = savedWorldMap[item.id]?.resolveShelfStatus(),
            historyState = uiState.worldIssueHistoryState,
            onSaveWithStatus = { status -> onSaveWorldComicWithStatus(item, status) },
            onToggleSave = { onToggleSaveWorldComic(item) },
            onRollRandom = {
                onRollRandomComicVine()
                selectedWorldIssue = null
                onClearWorldComicIssues()
            },
            onDismiss = {
                selectedWorldIssue = null
                onClearWorldComicIssues()
            }
        )
    }
}

@Composable
private fun EmptyDirectoryState(
    query: String,
    onClearFilters: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No comics matched \"$query\"",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Try clearing filters or tap Random Match for a surprise.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = onClearFilters,
                modifier = Modifier.testTag("explore_clear_filters_btn")
            ) {
                Text("Clear Filters")
            }
        }
    }
}

@Composable
private fun ExploreIssueTimelineCard(
    entry: IssueTimelineEntry,
    onClick: () -> Unit
) {
    val comicColors = LocalComicColors.current
    val accent = Color(entry.accentHex)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("explore_issue_${entry.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (entry.isUpcoming) comicColors.sundayYellow else accent.copy(alpha = 0.16f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Issue #${entry.issueNumber}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (entry.isUpcoming) comicColors.onSundayYellow else accent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.seriesTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Release Date: ${entry.releaseDate} • ${entry.statusLabel}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
