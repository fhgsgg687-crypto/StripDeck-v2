package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.model.LibraryShelfStatus
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import com.example.ui.ComicUiState
import com.example.ui.WorldVaultUiState
import com.example.ui.components.ComicSeriesCard
import com.example.ui.components.RandomActionPillButton
import com.example.ui.theme.LocalComicColors

private val globalSearchTopics = listOf(
    "Marvel", "DC Comics", "Image", "Dark Horse", "Amar Chitra Katha",
    "Chacha Chaudhary", "Tinkle", "Tintin", "Asterix", "Batman",
    "Spider-Man", "Superman", "One Piece", "Naruto", "Solo Leveling"
)

@Composable
fun TodayScreen(
    uiState: ComicUiState,
    onWorldQueryChange: (String) -> Unit,
    onWorldSearchSubmit: () -> Unit,
    onSelectWorldSource: (WorldSource?) -> Unit,
    onRollRandomWorldAny: () -> Unit,
    onRandomSeries: () -> Unit,
    onToggleSaveWorldComic: (WorldComicItem) -> Unit,
    onSaveWorldComicWithStatus: (WorldComicItem, LibraryShelfStatus) -> Unit,
    onLoadWorldComicIssues: (WorldComicItem) -> Unit,
    onClearWorldComicIssues: () -> Unit,
    onOpenSeries: (String) -> Unit,
    onToggleFavoriteSeries: (String) -> Unit,
    onLoadMoreWorld: () -> Unit,
    modifier: Modifier = Modifier
) {
    val comicColors = LocalComicColors.current
    var selectedItem by remember { mutableStateOf<WorldComicItem?>(null) }
    val savedMap = remember(uiState.preferences.savedWorldComics) {
        uiState.preferences.savedWorldComics.associateBy { it.id }
    }
    val localSeriesMatches = remember(uiState.worldQuery, uiState.allSeries) {
        val q = uiState.worldQuery.trim().lowercase()
        if (q.isBlank()) {
            emptyList()
        } else {
            uiState.allSeries.filter {
                it.title.lowercase().contains(q) ||
                    it.creator.lowercase().contains(q) ||
                    it.genre.label.lowercase().contains(q)
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Search All World Comics",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "Marvel, DC, Indie, Indian Classics, European BD, Manga, Manhwa & Archives",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.worldQuery,
                            onValueChange = onWorldQueryChange,
                            placeholder = { Text("Search any comic, publisher, or character...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            trailingIcon = {
                                if (uiState.worldQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            onWorldQueryChange("")
                                            onWorldSearchSubmit()
                                        },
                                        modifier = Modifier.testTag("search_tab_clear_btn")
                                    ) {
                                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { onWorldSearchSubmit() }),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_tab_input")
                        )
                        Button(
                            onClick = onWorldSearchSubmit,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("search_tab_go_btn")
                        ) {
                            Text("Search")
                        }
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(globalSearchTopics) { topic ->
                            AssistChip(
                                onClick = {
                                    onWorldQueryChange(topic)
                                    onWorldSearchSubmit()
                                },
                                label = { Text(topic) }
                            )
                        }
                    }
                }
            }

            // Quick Random Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RandomActionPillButton(
                        label = "Random World",
                        onClick = onRollRandomWorldAny,
                        icon = Icons.Filled.Public,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f),
                        testTag = "search_random_world_btn"
                    )
                    RandomActionPillButton(
                        label = "Random Series",
                        onClick = onRandomSeries,
                        icon = Icons.Filled.Casino,
                        containerColor = comicColors.sundayYellow,
                        contentColor = comicColors.onSundayYellow,
                        modifier = Modifier.weight(1f),
                        testTag = "search_random_series_btn"
                    )
                }
            }

            // World Source Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = uiState.worldSourceFilter == null,
                            onClick = { onSelectWorldSource(null) },
                            label = { Text("All World") }
                        )
                    }
                    items(WorldSource.entries) { src ->
                        FilterChip(
                            selected = uiState.worldSourceFilter == src,
                            onClick = {
                                onSelectWorldSource(if (uiState.worldSourceFilter == src) null else src)
                            },
                            label = { Text(src.label) }
                        )
                    }
                }
            }

            if (localSeriesMatches.isNotEmpty()) {
                item {
                    Text(
                        text = "Curated Series Matches (${localSeriesMatches.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(localSeriesMatches, key = { "st_local_${it.id}" }) { series ->
                    ComicSeriesCard(
                        series = series,
                        isFavorite = series.id in uiState.preferences.favoriteSeriesIds,
                        onClick = { onOpenSeries(series.id) },
                        onToggleFavorite = { onToggleFavoriteSeries(series.id) }
                    )
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
                                Text("Could not search world catalogs", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = vaultState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = onWorldSearchSubmit) {
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
                                modifier = Modifier.fillMaxWidth()
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
                                            text = "Get a free key at comicvine.gamespot.com/api/ and add COMICVINE_API_KEY in AI Studio Secrets to unlock live Marvel, DC, Image & Indie single issues.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(onClick = { onSelectWorldSource(null) }) {
                                            Text("Browse All World Sources")
                                        }
                                    } else {
                                        OutlinedButton(onClick = {
                                            onWorldQueryChange("")
                                            onWorldSearchSubmit()
                                        }) {
                                            Text("Reset Search")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "World Catalog Results (${vaultState.items.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(vaultState.items, key = { "st_world_${it.id}" }) { item ->
                            WorldComicCard(
                                item = item,
                                savedStatus = savedMap[item.id]?.resolveShelfStatus(),
                                onToggleSave = { onToggleSaveWorldComic(item) },
                                onClick = { selectedItem = item }
                            )
                        }
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
                                        onClick = onLoadMoreWorld,
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                    ) {
                                        Text("Load More World Results (Page ${uiState.worldPage + 1})")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
