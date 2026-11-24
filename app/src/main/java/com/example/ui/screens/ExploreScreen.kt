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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ComicGenre
import com.example.model.ExploreSortOption
import com.example.ui.ComicUiState
import com.example.ui.components.ComicSeriesCard
import com.example.ui.components.RandomActionPillButton
import com.example.ui.components.StripListPreviewCard

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
    modifier: Modifier = Modifier
) {
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
                        text = "Comics Directory",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "${uiState.filteredSeries.size} series matching",
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onQueryChange,
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
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

            Spacer(modifier = Modifier.height(10.dp))

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

            // Sort Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExploreSortOption.entries) { sort ->
                    FilterChip(
                        selected = uiState.sortOption == sort,
                        onClick = { onSelectSort(sort) },
                        label = { Text(sort.label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.filteredSeries.isEmpty() && uiState.filteredStrips.isEmpty()) {
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
                            text = "No comics matched \"${uiState.searchQuery}\"",
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
                                text = "Matching Strips (${uiState.filteredStrips.size})",
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
}
