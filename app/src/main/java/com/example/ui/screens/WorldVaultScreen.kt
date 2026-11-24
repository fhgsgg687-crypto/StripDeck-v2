package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.model.LibraryShelfStatus
import com.example.model.UpcomingStripRelease
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import com.example.ui.ComicUiState
import com.example.ui.WorldVaultUiState
import com.example.ui.components.RandomActionPillButton
import com.example.ui.theme.InkBlack
import com.example.ui.theme.LocalComicColors

private val quickWorldTopics = listOf(
    "Marvel", "DC Comics", "Amar Chitra Katha", "Chacha Chaudhary", "Tinkle",
    "Garfield", "Peanuts", "Tintin", "Asterix", "Batman", "Spider-Man",
    "One Piece", "Naruto", "Superman", "Humor"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WorldVaultScreen(
    uiState: ComicUiState,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onSelectSource: (WorldSource?) -> Unit,
    onRollRandomWorldBook: () -> Unit,
    onRollRandomUpcoming: () -> Unit,
    onRollRandomWorldManga: () -> Unit,
    onRollRandomPublicDomain: () -> Unit,
    onToggleSaveWorldComic: (WorldComicItem) -> Unit,
    onSaveWorldComicWithStatus: (WorldComicItem, LibraryShelfStatus) -> Unit,
    onOpenSeries: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRetryLoad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val comicColors = LocalComicColors.current
    var selectedItem by remember { mutableStateOf<WorldComicItem?>(null) }
    val savedMap = remember(uiState.preferences.savedWorldComics) {
        uiState.preferences.savedWorldComics.associateBy { it.id }
    }

    fun openExternalUrl(url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "World Comics Hub",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                    Text(
                        text = "Global Comics, Indian Classics, European BD, Public Domain Vault & Manga • ${savedMap.size} Saved in Library",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // World Search Bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.worldQuery,
                            onValueChange = onQueryChange,
                            placeholder = { Text("Search any world comic, title, or genre...") },
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
                            shape = RoundedCornerShape(16.dp),
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
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("world_search_go_btn")
                        ) {
                            Text("Search")
                        }
                    }

                    // Quick World Search Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(quickWorldTopics) { topic ->
                            AssistChip(
                                onClick = {
                                    onQueryChange(topic)
                                    onSearchSubmit()
                                },
                                label = { Text(topic) }
                            )
                        }
                    }
                }
            }

            // 4 World Random Buttons (2x2)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Random World",
                            onClick = onRollRandomWorldBook,
                            icon = Icons.Filled.Public,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_book_btn"
                        )
                        RandomActionPillButton(
                            label = "Random Upcoming",
                            onClick = onRollRandomUpcoming,
                            icon = Icons.Filled.Schedule,
                            containerColor = comicColors.mint,
                            contentColor = Color.White,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_upcoming_btn"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RandomActionPillButton(
                            label = "Random Manga",
                            onClick = onRollRandomWorldManga,
                            icon = Icons.Filled.Casino,
                            containerColor = comicColors.vermilion,
                            contentColor = Color.White,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_manga_btn"
                        )
                        RandomActionPillButton(
                            label = "Random Classic",
                            onClick = onRollRandomPublicDomain,
                            icon = Icons.Filled.AutoStories,
                            containerColor = comicColors.sundayYellow,
                            contentColor = InkBlack,
                            modifier = Modifier.weight(1f),
                            testTag = "world_random_classic_btn"
                        )
                    }
                    if (uiState.isWorldRandomLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // World Random Spotlight Card
            uiState.worldSpotlight?.let { spotlight ->
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(2.dp, InkBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedItem = spotlight }
                            .testTag("world_spotlight_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Surface(
                                color = comicColors.sundayYellow,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.2.dp, InkBlack)
                            ) {
                                Text(
                                    text = "RANDOM WORLD PICK • ${spotlight.source.label.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = InkBlack,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                WorldCoverBox(
                                    coverUrl = spotlight.coverUrl,
                                    title = spotlight.title,
                                    width = 88.dp,
                                    height = 124.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = spotlight.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${spotlight.creator} • ${spotlight.yearOrDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = spotlight.statsLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = spotlight.synopsis,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            val spotlightSaved = spotlight.id in savedMap
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onToggleSaveWorldComic(spotlight) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (spotlightSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (spotlightSaved) "In Library" else "Save")
                                }
                                Button(
                                    onClick = { openExternalUrl(spotlight.externalReadUrl) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Comic")
                                }
                            }
                        }
                    }
                }
            }

            // World Source / Region Filter Chips
            item {
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

            // Upcoming Strip Schedule Banner (visible in Upcoming or All World)
            if ((uiState.worldSourceFilter == null || uiState.worldSourceFilter == WorldSource.UPCOMING) &&
                uiState.worldQuery.isBlank() &&
                uiState.upcomingStrips.isNotEmpty()
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
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
                                text = "Upcoming Comic Drops (Nov–Dec 2026)",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
                            modifier = Modifier.fillMaxWidth()
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
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No matching world comics found", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(onClick = {
                                        onQueryChange("")
                                        onSearchSubmit()
                                    }) {
                                        Text("Reset World Search")
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
                                onClick = { selectedItem = item }
                            )
                        }

                        // Load More Pagination Button
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
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

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    selectedItem?.let { item ->
        ModalBottomSheet(onDismissRequest = { selectedItem = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    WorldCoverBox(
                        coverUrl = item.coverUrl,
                        title = item.title,
                        width = 96.dp,
                        height = 136.dp
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
                            text = "${item.creator} • ${item.yearOrDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.statsLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = comicColors.vermilion
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.genres.forEach { genre ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = item.synopsis,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 7,
                    overflow = TextOverflow.Ellipsis
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val activeShelf = savedMap[item.id]?.resolveShelfStatus()
                    Text(
                        text = if (activeShelf != null) {
                            "In Your Digital Library (${activeShelf.label})"
                        } else {
                            "Add to Digital Library Shelf:"
                        },
                        style = MaterialTheme.typography.labelLarge
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(LibraryShelfStatus.entries) { status ->
                            FilterChip(
                                selected = activeShelf == status,
                                onClick = { onSaveWorldComicWithStatus(item, status) },
                                label = { Text(status.label) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isSaved = item.id in savedMap
                    OutlinedButton(
                        onClick = { onToggleSaveWorldComic(item) },
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isSaved) "Saved" else "Save")
                    }
                    Button(
                        onClick = { openExternalUrl(item.externalReadUrl) },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            when {
                                item.source == WorldSource.PUBLIC_DOMAIN -> "Read on Internet Archive"
                                item.externalReadUrl.contains("openlibrary.org") -> "Open in Open Library"
                                else -> "View on MyAnimeList"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WorldCoverBox(
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

@Composable
private fun WorldComicCard(
    item: WorldComicItem,
    savedStatus: LibraryShelfStatus?,
    onToggleSave: () -> Unit,
    onClick: () -> Unit
) {
    val comicColors = LocalComicColors.current
    val badgeColor = when (item.source) {
        WorldSource.PUBLIC_DOMAIN -> Color(0xFF795548)
        WorldSource.WORLD_CATALOG -> comicColors.mint
        WorldSource.UPCOMING -> Color(0xFF00897B)
        WorldSource.MANGA -> MaterialTheme.colorScheme.primary
        WorldSource.MANHWA -> comicColors.vermilion
        WorldSource.MANHUA -> Color(0xFF8E24AA)
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.28f)),
        modifier = Modifier
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
                height = 100.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.source.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (savedStatus != null) {
                        Surface(
                            color = comicColors.sundayYellow,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = savedStatus.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = InkBlack,
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
                    text = "${item.creator} • ${item.yearOrDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.genres.take(3).joinToString(" • "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.statsLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    contentDescription = if (savedStatus != null) "Remove from Library" else "Save to Library",
                    tint = if (savedStatus != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.7f)),
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
