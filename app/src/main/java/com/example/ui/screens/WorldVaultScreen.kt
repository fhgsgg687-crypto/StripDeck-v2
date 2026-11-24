package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.HomeSpotlightTab
import com.example.model.IssueTimelineEntry
import com.example.model.LibraryShelfStatus
import com.example.model.UpcomingStripRelease
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import com.example.ui.ComicUiState
import com.example.ui.WorldIssueHistoryState
import com.example.ui.WorldVaultUiState
import com.example.ui.components.RandomActionPillButton
import com.example.ui.theme.LocalComicColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorldVaultScreen(
    uiState: ComicUiState,
    onSelectHomeTab: (HomeSpotlightTab) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onSelectSource: (WorldSource?) -> Unit,
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
    onOpenSeries: (String) -> Unit,
    onOpenStrip: (String) -> Unit,
    onOpenSearchTab: () -> Unit,
    onLoadMore: () -> Unit,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    var selectedItem by remember { mutableStateOf<WorldComicItem?>(null) }
    var showIssueCalendarSheet by remember { mutableStateOf(false) }
    val savedMap = remember(uiState.preferences.savedWorldComics) {
        uiState.preferences.savedWorldComics.associateBy { it.id }
    }

    val liveItems = (uiState.worldVaultState as? WorldVaultUiState.Success)?.items.orEmpty()
    val heroItem: WorldComicItem? = remember(uiState.homeSpotlightTab, uiState.worldSpotlight, liveItems) {
        if (uiState.worldSpotlight != null && uiState.homeSpotlightTab == HomeSpotlightTab.FOR_YOU) {
            uiState.worldSpotlight
        } else {
            val coverCandidates = liveItems.filter { it.coverUrl != null }.ifEmpty { liveItems }
            when (uiState.homeSpotlightTab) {
                HomeSpotlightTab.FOR_YOU -> coverCandidates.getOrNull(0)
                HomeSpotlightTab.FEATURED -> coverCandidates.getOrNull(1) ?: coverCandidates.firstOrNull()
                HomeSpotlightTab.PICK_OF_THE_WEEK -> coverCandidates.getOrNull(2) ?: coverCandidates.firstOrNull()
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // League-Style Top Header Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            border = BorderStroke(1.2.dp, comicColors.sundayYellow)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = "StripDeck Collector Hub",
                                tint = comicColors.sundayYellow,
                                modifier = Modifier
                                    .padding(7.dp)
                                    .size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "StripDeck",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "World Comics • ${savedMap.size} Tracked",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { showIssueCalendarSheet = true },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("open_issue_calendar_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = "Issue and Release Date Calendar"
                            )
                        }
                        IconButton(
                            onClick = onRollRandomWorldAny,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("header_random_dice_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Casino,
                                contentDescription = "Roll Random World Comic",
                                tint = comicColors.sundayYellow
                            )
                        }
                        IconButton(
                            onClick = onOpenSearchTab,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("header_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search World Comics"
                            )
                        }
                    }
                }
            }

            // Full-Bleed League-Style Hero Spotlight with Top 3-Segment Tabs ("For You" | "Featured" | "Pick of the Week")
            item {
                LeagueHeroSpotlightCard(
                    activeTab = uiState.homeSpotlightTab,
                    heroItem = heroItem,
                    isSaved = heroItem?.id in savedMap,
                    onSelectTab = onSelectHomeTab,
                    onRollRandomHero = onRollRandomWorldAny,
                    onToggleSaveHero = { heroItem?.let(onToggleSaveWorldComic) },
                    onOpenHeroDetail = { heroItem?.let { selectedItem = it } },
                    onOpenFirstUpcoming = {
                        uiState.upcomingStrips.firstOrNull()?.let { onOpenSeries(it.seriesId) }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 4 Quick Random Comic Buttons (Always Accessible Right Below Hero)
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Random World Buttons",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${uiState.preferences.randomRollCount} rolls",
                            style = MaterialTheme.typography.labelSmall,
                            color = comicColors.sundayYellow
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Random World",
                            onClick = onRollRandomWorldBook,
                            icon = Icons.Filled.Public,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_book_btn"
                        )
                        RandomActionPillButton(
                            label = "Random Drop",
                            onClick = onRollRandomUpcoming,
                            icon = Icons.Filled.Schedule,
                            containerColor = comicColors.mint,
                            contentColor = Color(0xFF06201D),
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_upcoming_btn"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Random Manga",
                            onClick = onRollRandomWorldManga,
                            icon = Icons.Filled.Casino,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_manga_btn"
                        )
                        RandomActionPillButton(
                            label = "Random Classic",
                            onClick = onRollRandomPublicDomain,
                            icon = Icons.Filled.AutoStories,
                            containerColor = comicColors.sundayYellow,
                            contentColor = comicColors.onSundayYellow,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_classic_btn"
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
                        testTag = "world_random_comicvine_btn"
                    )
                    if (uiState.isWorldRandomLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Horizontal Portrait Cover Carousel ("New & Trending World Comics" — matches "What's New from Friends" in screenshot)
            if (liveItems.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New & Trending Comics",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onOpenSearchTab) {
                                Text(
                                    text = "Search All",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(liveItems.take(12), key = { "carousel_${it.id}" }) { item ->
                                LeaguePortraitCoverCard(
                                    item = item,
                                    isSaved = item.id in savedMap,
                                    onToggleSave = { onToggleSaveWorldComic(item) },
                                    onClick = { selectedItem = item }
                                )
                            }
                        }
                    }
                }
            }

            // "Challenges & Collector Tracker" Card (matches "Challenges & Events" in screenshot)
            item {
                val trackedTotal = savedMap.size + uiState.preferences.favoriteSeriesIds.size + uiState.preferences.bookmarkedStripIds.size
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.2.dp, comicColors.inkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { showIssueCalendarSheet = true }
                        .testTag("challenges_calendar_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = comicColors.sundayYellow.copy(alpha = 0.16f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = null,
                                    tint = comicColors.sundayYellow,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Release Calendar • ${uiState.issueCalendar.size} Issues",
                                style = MaterialTheme.typography.labelMedium,
                                color = comicColors.sundayYellow
                            )
                            Text(
                                text = "2026 Comic Issue & Release Tracker",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$trackedTotal items in Pull List & Collection • Tap for all issue dates",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Upcoming Comic Drops Row
            if (uiState.upcomingStrips.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = comicColors.mint,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Upcoming Issue Drops (Nov–Dec 2026)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.upcomingStrips, key = { it.id }) { release ->
                                UpcomingStripScheduleCard(
                                    release = release,
                                    onClick = { onOpenSeries(release.seriesId) }
                                )
                            }
                        }
                    }
                }
            }

            // World Source Filter Chips + Quick Filter Input
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "World Comics Database",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.worldQuery,
                            onValueChange = onQueryChange,
                            placeholder = { Text("Filter Marvel, DC, Manga, Tintin, Indian...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            trailingIcon = {
                                if (uiState.worldQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            onQueryChange("")
                                            onSearchSubmit()
                                        },
                                        modifier = Modifier.testTag("world_search_clear_btn")
                                    ) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("world_search_input")
                        )
                        Button(
                            onClick = onSearchSubmit,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("world_search_go_btn")
                        ) {
                            Text("Go")
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.worldSourceFilter == null,
                                onClick = { onSelectSource(null) },
                                label = { Text("All World") },
                                modifier = Modifier.testTag("world_source_all")
                            )
                        }
                        items(WorldSource.entries) { src ->
                            FilterChip(
                                selected = uiState.worldSourceFilter == src,
                                onClick = {
                                    onSelectSource(if (uiState.worldSourceFilter == src) null else src)
                                },
                                label = { Text(src.label) },
                                modifier = Modifier.testTag("world_source_${src.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            when (val vaultState = uiState.worldVaultState) {
                is WorldVaultUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is WorldVaultUiState.Error -> {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Could not load world comics",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = vaultState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = onRetryLoad) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
                is WorldVaultUiState.Success -> {
                    if (vaultState.items.isEmpty()) {
                        item {
                            val isCvSetupNeeded =
                                uiState.worldSourceFilter == WorldSource.COMICVINE && !uiState.isComicVineConfigured
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isCvSetupNeeded) {
                                            "ComicVine Single-Issue API Setup"
                                        } else {
                                            "No matching world comics found"
                                        },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (isCvSetupNeeded) {
                                        Text(
                                            text = "Get a free API key at comicvine.gamespot.com/api/ and add COMICVINE_API_KEY in AI Studio Secrets to unlock live Marvel, DC, Image & Indie single issues with exact store release dates.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(onClick = { onSelectSource(null) }) {
                                            Text("Browse All World Sources")
                                        }
                                    } else {
                                        OutlinedButton(onClick = {
                                            onQueryChange("")
                                            onSearchSubmit()
                                        }) {
                                            Text("Reset Filter")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(vaultState.items, key = { it.id }) { item ->
                            WorldComicCard(
                                item = item,
                                savedStatus = savedMap[item.id]?.resolveShelfStatus(),
                                onToggleSave = { onToggleSaveWorldComic(item) },
                                onClick = { selectedItem = item },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isLoadingMoreWorld) {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                } else {
                                    OutlinedButton(
                                        onClick = onLoadMore,
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                            .testTag("world_load_more_btn")
                                    ) {
                                        Text("Load More World Comics (Page ${uiState.worldPage + 1})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showIssueCalendarSheet) {
        IssueCalendarModalSheet(
            entries = uiState.issueCalendar,
            onDismiss = { showIssueCalendarSheet = false },
            onOpenEntry = { entry ->
                showIssueCalendarSheet = false
                if (entry.stripId != null) {
                    onOpenStrip(entry.stripId)
                } else {
                    onOpenSeries(entry.seriesId)
                }
            }
        )
    }

    selectedItem?.let { item ->
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
                selectedItem = null
                onClearWorldComicIssues()
            },
            onDismiss = {
                selectedItem = null
                onClearWorldComicIssues()
            }
        )
    }
}

@Composable
private fun LeagueHeroSpotlightCard(
    activeTab: HomeSpotlightTab,
    heroItem: WorldComicItem?,
    isSaved: Boolean,
    onSelectTab: (HomeSpotlightTab) -> Unit,
    onRollRandomHero: () -> Unit,
    onToggleSaveHero: () -> Unit,
    onOpenHeroDetail: () -> Unit,
    onOpenFirstUpcoming: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, comicColors.inkBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable {
                if (heroItem != null) onOpenHeroDetail() else onOpenFirstUpcoming()
            }
            .testTag("league_hero_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
        ) {
            if (heroItem?.coverUrl != null) {
                AsyncImage(
                    model = heroItem.coverUrl,
                    contentDescription = heroItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_newsstand),
                    contentDescription = "World comics spotlight artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Deep Atmospheric Scrim matching the screenshot
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xCC0D111A),
                                Color(0x400D111A),
                                Color(0xF00D111A)
                            )
                        )
                    )
            )

            // Floating 3-Segment Selector ("For You | Featured | Pick of the Week")
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xB8141A26),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    HomeSpotlightTab.entries.forEach { tab ->
                        val selected = tab == activeTab
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) Color.White.copy(alpha = 0.18f) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectTab(tab) }
                                .testTag("hero_tab_${tab.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (selected) Color.White else Color.White.copy(alpha = 0.72f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // Hero Bottom Details
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = activeTab.label.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    heroItem?.issueLabel?.let { issue ->
                        Surface(
                            color = comicColors.sundayYellow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = issue,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = comicColors.onSundayYellow,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = heroItem?.title ?: "Explore Global Comic Releases",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (heroItem != null) {
                        "${heroItem.creator} • Release: ${heroItem.yearOrDate}"
                    } else {
                        "Track issue numbers, release dates, and global series."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = heroItem?.synopsis ?: "Global comic catalog database without free reading.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRollRandomHero,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = comicColors.sundayYellow,
                            contentColor = comicColors.onSundayYellow
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("hero_roll_random_btn")
                    ) {
                        Icon(Icons.Filled.Casino, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Random",
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleSaveHero,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("hero_pull_btn")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSaved) "Saved" else "Pull List",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaguePortraitCoverCard(
    item: WorldComicItem,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    val comicColors = LocalComicColors.current

    Column(
        modifier = Modifier
            .width(148.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("portrait_world_card_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (item.coverUrl != null) {
                AsyncImage(
                    model = item.coverUrl,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = item.title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent, Color(0xE80A0E17))
                        )
                    )
            )

            // Save Pill top-right
            Surface(
                shape = CircleShape,
                color = Color(0xB30D111A),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onToggleSave)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Save to Pull List",
                        tint = if (isSaved) comicColors.sundayYellow else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Issue badge top-left
            item.issueLabel?.let { issue ->
                Surface(
                    color = comicColors.sundayYellow,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                ) {
                    Text(
                        text = issue,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = comicColors.onSundayYellow,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.creator,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(4) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = comicColors.sundayYellow,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier.size(12.dp)
                )
            }
            Text(
                text = item.yearOrDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorldComicCollectorSheet(
    item: WorldComicItem,
    savedStatus: LibraryShelfStatus?,
    historyState: WorldIssueHistoryState,
    onSaveWithStatus: (LibraryShelfStatus) -> Unit,
    onToggleSave: () -> Unit,
    onRollRandom: () -> Unit,
    onDismiss: () -> Unit
) {
    val comicColors = LocalComicColors.current

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                WorldCoverBox(
                    coverUrl = item.coverUrl,
                    title = item.title,
                    width = 96.dp,
                    height = 138.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.source.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(item.title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = item.creator,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item.issueLabel?.let { issue ->
                            Surface(
                                color = comicColors.sundayYellow,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = issue,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = comicColors.onSundayYellow,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Release: ${item.yearOrDate}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.statsLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = comicColors.sundayYellow
                    )
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item.genres.forEach { genre ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = item.synopsis,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )

            // Live Issues, Editions & Release Dates Section (Metadata Only — No Free Reading)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, comicColors.inkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Issues, Editions & Release Dates",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    when (historyState) {
                        is WorldIssueHistoryState.Idle,
                        is WorldIssueHistoryState.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    text = "Fetching live issue & release dates...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        is WorldIssueHistoryState.Error -> {
                            Text(
                                text = "Release date: ${item.yearOrDate}. Live edition lookup unavailable (${historyState.message}).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is WorldIssueHistoryState.Success -> {
                            if (historyState.editions.isEmpty()) {
                                Text(
                                    text = "Indexed Release Date: ${item.yearOrDate}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                historyState.editions.take(8).forEach { ed ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = ed.issueOrEditionTitle,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    modifier = Modifier.weight(1f),
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    color = comicColors.sundayYellow,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        text = ed.releaseDate,
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = comicColors.onSundayYellow,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = ed.publisherOrDetail,
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

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (savedStatus != null) {
                        "In Your Pull List / Collection (${savedStatus.label})"
                    } else {
                        "Track in Pull List or Collection:"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(LibraryShelfStatus.entries) { status ->
                        FilterChip(
                            selected = savedStatus == status,
                            onClick = { onSaveWithStatus(status) },
                            label = { Text(status.label) }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (savedStatus != null) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (savedStatus != null) "Saved" else "Add to Collection")
                }
                Button(
                    onClick = onRollRandom,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Casino,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Next Random")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WorldCoverBox(
    coverUrl: String?,
    title: String,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
) {
    if (coverUrl != null) {
        AsyncImage(
            model = coverUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    } else {
        Box(
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorldComicCard(
    item: WorldComicItem,
    savedStatus: LibraryShelfStatus?,
    onToggleSave: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    val badgeColor = when (item.source) {
        WorldSource.COMICVINE -> comicColors.sundayYellow
        WorldSource.PUBLIC_DOMAIN -> Color(0xFFA1887F)
        WorldSource.WORLD_CATALOG -> comicColors.mint
        WorldSource.UPCOMING -> Color(0xFF2DD4BF)
        WorldSource.MANGA -> MaterialTheme.colorScheme.primary
        WorldSource.MANHWA -> comicColors.vermilion
        WorldSource.MANHUA -> Color(0xFFC084FC)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, comicColors.inkBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("world_item_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorldCoverBox(
                coverUrl = item.coverUrl,
                title = item.title,
                width = 72.dp,
                height = 102.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.source.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    item.issueLabel?.let { issue ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = issue,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (savedStatus != null) {
                        Surface(
                            color = comicColors.sundayYellow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = savedStatus.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = comicColors.onSundayYellow,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.creator,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Release: ${item.yearOrDate}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.genres.take(3).joinToString(" • "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(
                onClick = onToggleSave,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("world_save_btn_${item.id}")
            ) {
                Icon(
                    imageVector = if (savedStatus != null) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                    contentDescription = if (savedStatus != null) "Remove from Collection" else "Add to Collection",
                    tint = if (savedStatus != null) comicColors.sundayYellow else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UpcomingStripScheduleCard(
    release: UpcomingStripRelease,
    onClick: () -> Unit
) {
    val comicColors = LocalComicColors.current
    val accent = Color(release.accentHex)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.65f)),
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("upcoming_card_${release.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (release.isSundaySpecial) comicColors.sundayYellow else accent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = release.countdownLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (release.isSundaySpecial) comicColors.onSundayYellow else accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = release.releaseDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = release.seriesTitle,
                style = MaterialTheme.typography.labelMedium,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Issue #${release.stripNumber} • ${release.title}",
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = release.teaser,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IssueCalendarModalSheet(
    entries: List<IssueTimelineEntry>,
    onDismiss: () -> Unit,
    onOpenEntry: (IssueTimelineEntry) -> Unit
) {
    val comicColors = LocalComicColors.current
    var filterQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf(0) }

    val q = filterQuery.trim().lowercase().removePrefix("#")
    val filtered = remember(entries, filterQuery, filterMode) {
        entries.filter { entry ->
            val matchesMode = when (filterMode) {
                1 -> entry.isUpcoming
                2 -> !entry.isUpcoming
                else -> true
            }
            val matchesQ = q.isEmpty() ||
                entry.issueNumber.toString().contains(q) ||
                entry.releaseDate.lowercase().contains(q) ||
                entry.seriesTitle.lowercase().contains(q) ||
                entry.title.lowercase().contains(q)
            matchesMode && matchesQ
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = comicColors.sundayYellow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Issue # & Release Date Calendar",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Which issue released on which date (${filtered.size} shown)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = filterQuery,
                onValueChange = { filterQuery = it },
                placeholder = { Text("Search #issue number, date (Nov 18), or comic...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (filterQuery.isNotEmpty()) {
                        IconButton(onClick = { filterQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("calendar_search_input")
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("All (${entries.size})") }
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("Upcoming (${entries.count { it.isUpcoming }})") }
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = { Text("Released (${entries.count { !it.isUpcoming }})") }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filtered, key = { it.id }) { entry ->
                    val accent = Color(entry.accentHex)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenEntry(entry) }
                            .testTag("calendar_entry_${entry.id}")
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
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
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
                                    style = MaterialTheme.typography.titleSmall,
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
            }
        }
    }
}
