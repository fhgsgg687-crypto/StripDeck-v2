package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.LibraryShelfStatus
import com.example.model.SavedWorldComic
import com.example.model.WorldSource
import com.example.ui.ComicUiState
import com.example.ui.components.ComicSeriesCard
import com.example.ui.components.RandomActionPillButton
import com.example.ui.components.StripListPreviewCard
import com.example.ui.theme.InkBlack
import com.example.ui.theme.LocalComicColors

@Composable
fun SavedScreen(
    uiState: ComicUiState,
    onOpenSeries: (String) -> Unit,
    onOpenStrip: (String) -> Unit,
    onToggleFavoriteSeries: (String) -> Unit,
    onToggleBookmarkStrip: (String) -> Unit,
    onUpdateWorldComicStatus: (String, LibraryShelfStatus) -> Unit,
    onRemoveWorldComic: (String) -> Unit,
    onOpenWorldHub: () -> Unit,
    onRandomFromSaved: () -> Unit,
    onDiscoverRandomAny: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val comicColors = LocalComicColors.current
    var libraryQuery by remember { mutableStateOf("") }
    var selectedShelf by remember { mutableStateOf<LibraryShelfStatus?>(null) }

    val favSeriesAll = uiState.allSeries.filter { it.id in uiState.preferences.favoriteSeriesIds }
    val bookmarkedStripsAll = uiState.allSeries
        .flatMap { it.strips }
        .filter { it.id in uiState.preferences.bookmarkedStripIds }
    val savedWorldAll = uiState.preferences.savedWorldComics

    val q = libraryQuery.trim().lowercase()
    val filteredWorld = savedWorldAll.filter { entry ->
        val matchesShelf = selectedShelf == null || entry.resolveShelfStatus() == selectedShelf
        val matchesQuery = q.isEmpty() ||
            entry.title.lowercase().contains(q) ||
            entry.creator.lowercase().contains(q) ||
            entry.genres.any { it.lowercase().contains(q) }
        matchesShelf && matchesQuery
    }
    val filteredFavSeries = if (q.isEmpty() && selectedShelf == null) {
        favSeriesAll
    } else if (selectedShelf != null) {
        emptyList()
    } else {
        favSeriesAll.filter {
            it.title.lowercase().contains(q) || it.creator.lowercase().contains(q)
        }
    }
    val filteredBookmarkedStrips = if (q.isEmpty() && selectedShelf == null) {
        bookmarkedStripsAll
    } else if (selectedShelf != null) {
        emptyList()
    } else {
        bookmarkedStripsAll.filter {
            it.title.lowercase().contains(q)
        }
    }

    val totalSavedCount = savedWorldAll.size + favSeriesAll.size + bookmarkedStripsAll.size

    fun openExternalUrl(url: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Digital Library",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        Text(
                            text = "$totalSavedCount collected items • World vault, series & strips",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (favSeriesAll.isNotEmpty() || bookmarkedStripsAll.isNotEmpty()) {
                        RandomActionPillButton(
                            label = "Random Saved",
                            onClick = onRandomFromSaved,
                            icon = Icons.Filled.Casino,
                            testTag = "saved_random_btn"
                        )
                    }
                }
            }

            // Stats Strip
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBadgeCard(
                        value = "${savedWorldAll.size}",
                        label = "World Vault",
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        value = "${favSeriesAll.size}",
                        label = "Fav Series",
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        value = "${bookmarkedStripsAll.size}",
                        label = "Bookmarks",
                        modifier = Modifier.weight(1f)
                    )
                    StatBadgeCard(
                        value = "${uiState.preferences.readStripIds.size}",
                        label = "Strips Read",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (totalSavedCount > 0) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = libraryQuery,
                            onValueChange = { libraryQuery = it },
                            placeholder = { Text("Filter your digital library...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            trailingIcon = {
                                if (libraryQuery.isNotEmpty()) {
                                    IconButton(onClick = { libraryQuery = "" }) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear filter")
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
                                .testTag("library_search_input")
                        )

                        if (savedWorldAll.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedShelf == null,
                                        onClick = { selectedShelf = null },
                                        label = { Text("All Shelves (${savedWorldAll.size})") },
                                        modifier = Modifier.testTag("shelf_filter_all")
                                    )
                                }
                                items(LibraryShelfStatus.entries) { status ->
                                    val count = savedWorldAll.count { it.resolveShelfStatus() == status }
                                    FilterChip(
                                        selected = selectedShelf == status,
                                        onClick = {
                                            selectedShelf = if (selectedShelf == status) null else status
                                        },
                                        label = { Text("${status.label} ($count)") },
                                        modifier = Modifier.testTag("shelf_filter_${status.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (totalSavedCount == 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Bookmarks,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.empty_saved_title),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stringResource(R.string.empty_saved_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onOpenWorldHub,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .testTag("empty_library_world_btn")
                            ) {
                                Icon(Icons.Filled.Public, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Browse World Comics Hub")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            RandomActionPillButton(
                                label = "Surprise Me with a Strip",
                                onClick = onDiscoverRandomAny,
                                testTag = "empty_saved_random_btn"
                            )
                        }
                    }
                }
            } else {
                if (filteredWorld.isNotEmpty()) {
                    item {
                        Text(
                            text = "World Vault Collection (${filteredWorld.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(filteredWorld, key = { "lib_world_${it.id}" }) { savedItem ->
                        SavedWorldLibraryCard(
                            entry = savedItem,
                            onChangeStatus = { newStatus ->
                                onUpdateWorldComicStatus(savedItem.id, newStatus)
                            },
                            onOpenExternal = { openExternalUrl(savedItem.externalReadUrl) },
                            onRemove = { onRemoveWorldComic(savedItem.id) }
                        )
                    }
                }

                if (filteredFavSeries.isNotEmpty()) {
                    item {
                        Text(
                            text = "Favorite Series (${filteredFavSeries.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(filteredFavSeries, key = { "fav_${it.id}" }) { series ->
                        ComicSeriesCard(
                            series = series,
                            isFavorite = true,
                            onClick = { onOpenSeries(series.id) },
                            onToggleFavorite = { onToggleFavoriteSeries(series.id) }
                        )
                    }
                }

                if (filteredBookmarkedStrips.isNotEmpty()) {
                    item {
                        Text(
                            text = "Bookmarked Strips (${filteredBookmarkedStrips.size})",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    items(filteredBookmarkedStrips, key = { "bm_${it.id}" }) { strip ->
                        val series = uiState.allSeries.find { it.id == strip.seriesId }
                        StripListPreviewCard(
                            strip = strip,
                            series = series,
                            isBookmarked = true,
                            onReadClick = { onOpenStrip(strip.id) },
                            onToggleBookmark = { onToggleBookmarkStrip(strip.id) }
                        )
                    }
                }

                if (filteredWorld.isEmpty() && filteredFavSeries.isEmpty() && filteredBookmarkedStrips.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No library entries match your current filter", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(onClick = {
                                    libraryQuery = ""
                                    selectedShelf = null
                                }) {
                                    Text("Show All Saved")
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

@Composable
private fun SavedWorldLibraryCard(
    entry: SavedWorldComic,
    onChangeStatus: (LibraryShelfStatus) -> Unit,
    onOpenExternal: () -> Unit,
    onRemove: () -> Unit
) {
    val comicColors = LocalComicColors.current
    val source = entry.resolveSource()
    val currentStatus = entry.resolveShelfStatus()

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.28f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_world_card_${entry.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                if (entry.coverUrl != null) {
                    AsyncImage(
                        model = entry.coverUrl,
                        contentDescription = entry.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(68.dp)
                            .height(94.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .width(68.dp)
                            .height(94.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = entry.title,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = source.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${entry.creator} • ${entry.yearOrDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = entry.statsLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Remove from Library",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(LibraryShelfStatus.entries) { status ->
                    val selected = status == currentStatus
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) comicColors.sundayYellow else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (selected) BorderStroke(1.dp, InkBlack) else null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onChangeStatus(status) }
                    ) {
                        Text(
                            text = status.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (selected) InkBlack else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onOpenExternal,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    when {
                        source == WorldSource.PUBLIC_DOMAIN -> "Read Free on Internet Archive"
                        entry.externalReadUrl.contains("openlibrary.org") -> "Open in Open Library"
                        else -> "View on MyAnimeList"
                    }
                )
            }
        }
    }
}

@Composable
private fun StatBadgeCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, comicColors.inkBorder.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
