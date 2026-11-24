package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.ui.ComicUiState
import com.example.ui.components.CharacterAvatarBadge
import com.example.ui.components.ComicSeriesCard
import com.example.ui.components.ComicStripPanelsView
import com.example.ui.components.RandomActionPillButton
import com.example.ui.components.StripListPreviewCard
import com.example.ui.components.resolveSeriesCoverRes
import com.example.ui.theme.InkBlack
import com.example.ui.theme.LocalComicColors

@Composable
fun TodayScreen(
    uiState: ComicUiState,
    onQueryChange: (String) -> Unit,
    onSelectGenre: (ComicGenre?) -> Unit,
    onClearFilters: () -> Unit,
    onOpenExploreSearch: () -> Unit,
    onOpenSeries: (String) -> Unit,
    onOpenStrip: (String) -> Unit,
    onRandomAnyStrip: () -> Unit,
    onRandomSeries: () -> Unit,
    onRandomFilteredSeries: () -> Unit,
    onRandomByGenre: (ComicGenre) -> Unit,
    onOpenRandomLabXkcd: () -> Unit,
    onOpenUpcomingWorld: () -> Unit,
    onRollFeaturedRandom: () -> Unit,
    onShuffleMix: () -> Unit,
    onToggleFavoriteSeries: (String) -> Unit,
    onToggleBookmarkStrip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    val isFiltering = uiState.searchQuery.isNotBlank() || uiState.selectedGenre != null

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
            // Masthead Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = stringResource(R.string.hero_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.28f))
                    ) {
                        IconButton(
                            onClick = onOpenExploreSearch,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("home_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = "Open full directory"
                            )
                        }
                    }
                }
            }

            // Live Search Bar & Genre Filter Chips (filter by title or genre)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onQueryChange,
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search comics"
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onQueryChange("") },
                                    modifier = Modifier.testTag("today_search_clear_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("today_search_input")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedGenre == null,
                                onClick = { onSelectGenre(null) },
                                label = { Text("All Genres") },
                                modifier = Modifier.testTag("today_genre_all")
                            )
                        }
                        items(ComicGenre.entries) { genre ->
                            FilterChip(
                                selected = uiState.selectedGenre == genre,
                                onClick = {
                                    onSelectGenre(if (uiState.selectedGenre == genre) null else genre)
                                },
                                label = { Text(genre.label) },
                                modifier = Modifier.testTag("today_genre_${genre.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            if (isFiltering) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Search Results",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "${uiState.filteredSeries.size} series • ${uiState.filteredStrips.size} strips",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TextButton(
                                onClick = onClearFilters,
                                modifier = Modifier.testTag("today_clear_filters_btn")
                            ) {
                                Text("Clear")
                            }
                            if (uiState.filteredSeries.isNotEmpty()) {
                                RandomActionPillButton(
                                    label = "Random Match",
                                    onClick = onRandomFilteredSeries,
                                    testTag = "today_random_match_btn"
                                )
                            }
                        }
                    }
                }

                if (uiState.filteredSeries.isEmpty() && uiState.filteredStrips.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.28f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No comics matched your filter",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Try searching another title or genre, or reset filters.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedButton(onClick = onClearFilters) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                } else {
                    items(uiState.filteredSeries, key = { "search_series_${it.id}" }) { series ->
                        ComicSeriesCard(
                            series = series,
                            isFavorite = series.id in uiState.preferences.favoriteSeriesIds,
                            onClick = { onOpenSeries(series.id) },
                            onToggleFavorite = { onToggleFavoriteSeries(series.id) }
                        )
                    }

                    if (uiState.filteredStrips.isNotEmpty()) {
                        item {
                            Text(
                                text = "Matching Comic Strips (${uiState.filteredStrips.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(uiState.filteredStrips.take(8), key = { "search_strip_${it.id}" }) { strip ->
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
            } else {

            // Hero Newsstand Card with Prominent Random Buttons
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(2.dp, InkBlack),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(168.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_newsstand),
                                contentDescription = "Sunday comics newsstand artwork",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                InkBlack.copy(alpha = 0.80f)
                                            )
                                        )
                                    )
                            )
                            Surface(
                                color = comicColors.sundayYellow,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, InkBlack),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "${uiState.allSeries.size} SERIES • ALL COMICS ARCHIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = InkBlack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Feeling Lucky? Read a Random Comic!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = "Jump straight into any strip, series, or live XKCD comic.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RandomActionPillButton(
                                label = stringResource(R.string.btn_random_strip),
                                onClick = onRandomAnyStrip,
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.weight(1f),
                                testTag = "hero_random_strip_btn"
                            )
                            RandomActionPillButton(
                                label = stringResource(R.string.btn_random_series),
                                onClick = onRandomSeries,
                                icon = Icons.Filled.MenuBook,
                                containerColor = comicColors.sundayYellow,
                                contentColor = InkBlack,
                                modifier = Modifier.weight(1f),
                                testTag = "hero_random_series_btn"
                            )
                        }
                    }
                }
            }

            // Quick Category Random Buttons Strip
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Instant Random Buttons",
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            QuickRandomGenreButton(
                                label = "Random Humor",
                                accent = comicColors.vermilion,
                                onClick = { onRandomByGenre(ComicGenre.HUMOR) },
                                testTag = "quick_random_humor"
                            )
                        }
                        item {
                            QuickRandomGenreButton(
                                label = "Random Animals",
                                accent = Color(0xFFFF8F00),
                                onClick = { onRandomByGenre(ComicGenre.ANIMALS) },
                                testTag = "quick_random_animals"
                            )
                        }
                        item {
                            QuickRandomGenreButton(
                                label = "Random Sci-Fi",
                                accent = MaterialTheme.colorScheme.primary,
                                onClick = { onRandomByGenre(ComicGenre.SCI_FI) },
                                testTag = "quick_random_scifi"
                            )
                        }
                        item {
                            QuickRandomGenreButton(
                                label = "Random Retro",
                                accent = Color(0xFF795548),
                                onClick = { onRandomByGenre(ComicGenre.RETRO) },
                                testTag = "quick_random_retro"
                            )
                        }
                        item {
                            QuickRandomGenreButton(
                                label = "Random Office",
                                accent = comicColors.mint,
                                onClick = { onRandomByGenre(ComicGenre.OFFICE) },
                                testTag = "quick_random_office"
                            )
                        }
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = InkBlack,
                                border = BorderStroke(1.5.dp, InkBlack),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable(onClick = onOpenRandomLabXkcd)
                                    .minimumInteractiveComponentSize()
                                    .testTag("quick_random_xkcd")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Public,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Live XKCD",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Today's Spotlight Strip (Inline Readable Multi-Panel!)
            uiState.featuredStrip?.let { featured ->
                val series = uiState.allSeries.find { it.id == featured.seriesId }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sunday Spotlight Strip",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "${series?.title ?: "Featured"} • #${featured.stripNumber} ${featured.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = onRollFeaturedRandom,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("spotlight_reroll_btn"),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Casino,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Swap Random", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onOpenStrip(featured.id) }
                                .testTag("featured_strip_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                ComicStripPanelsView(strip = featured, compactMode = true)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = featured.authorNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    TextButton(onClick = { onOpenStrip(featured.id) }) {
                                        Text("Open Reader")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Trending Series Carousel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Popular Comics Directory",
                            style = MaterialTheme.typography.titleLarge
                        )
                        TextButton(
                            onClick = onOpenExploreSearch,
                            modifier = Modifier.testTag("view_all_directory_btn")
                        ) {
                            Text("All ${uiState.allSeries.size} Series")
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.allSeries.take(8), key = { it.id }) { series ->
                            TrendingSeriesMiniCard(
                                series = series,
                                onClick = { onOpenSeries(series.id) }
                            )
                        }
                    }
                }
            }

            // Upcoming Comic Drops Carousel
            if (uiState.upcomingStrips.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = comicColors.mint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Upcoming Drops (Nov–Dec)",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            TextButton(
                                onClick = onOpenUpcomingWorld,
                                modifier = Modifier.testTag("today_see_upcoming_btn")
                            ) {
                                Text("World Upcoming")
                            }
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(uiState.upcomingStrips, key = { "today_up_${it.id}" }) { release ->
                                val accent = Color(release.accentHex)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.5.dp, accent.copy(alpha = 0.65f)),
                                    modifier = Modifier
                                        .width(208.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onOpenSeries(release.seriesId) }
                                        .testTag("today_upcoming_${release.id}")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = if (release.isSundaySpecial) comicColors.sundayYellow else accent.copy(alpha = 0.14f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = release.countdownLabel.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (release.isSundaySpecial) InkBlack else accent,
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
                                            text = "#${release.stripNumber} • ${release.title}",
                                            style = MaterialTheme.typography.titleSmall,
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

            // 3-Strip Random Shuffle Mix
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Random 3-Strip Mix",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    RandomActionPillButton(
                        label = stringResource(R.string.btn_shuffle_mix),
                        onClick = onShuffleMix,
                        icon = Icons.Filled.Shuffle,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        testTag = "shuffle_mix_btn"
                    )
                }
            }

            items(uiState.randomMixStrips, key = { it.id }) { strip ->
                val series = uiState.allSeries.find { it.id == strip.seriesId }
                StripListPreviewCard(
                    strip = strip,
                    series = series,
                    isBookmarked = strip.id in uiState.preferences.bookmarkedStripIds,
                    onReadClick = { onOpenStrip(strip.id) },
                    onToggleBookmark = { onToggleBookmarkStrip(strip.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun QuickRandomGenreButton(
    label: String,
    accent: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.14f),
        border = BorderStroke(1.5.dp, accent),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Casino,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = accent
            )
        }
    }
}

@Composable
private fun TrendingSeriesMiniCard(
    series: ComicSeries,
    onClick: () -> Unit
) {
    val comicColors = LocalComicColors.current
    val accent = Color(series.accentColorHex)
    val coverRes = resolveSeriesCoverRes(series.coverDrawableName)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.28f)),
        modifier = Modifier
            .width(156.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("trending_series_${series.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (coverRes != null) {
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = series.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(92.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    CharacterAvatarBadge(
                        archetype = series.mascotArchetype,
                        accentColor = accent,
                        size = 66.dp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = series.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = series.creator,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "#${series.rank} • ${series.launchYear}",
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
        }
    }
}
