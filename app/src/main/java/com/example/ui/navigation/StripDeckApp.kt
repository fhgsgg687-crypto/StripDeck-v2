package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Casino
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.model.WorldSource
import com.example.ui.ComicViewModel
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.RandomLabScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SeriesDetailScreen
import com.example.ui.screens.StripReaderScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.screens.WorldVaultScreen

object StripDeckRoutes {
    const val TODAY = "today"
    const val EXPLORE = "explore"
    const val WORLD_VAULT = "world_vault"
    const val RANDOM_LAB = "random_lab"
    const val SAVED = "saved"
    const val SERIES_DETAIL = "series/{seriesId}"
    const val STRIP_READER = "reader/{stripId}"

    fun seriesRoute(seriesId: String) = "series/$seriesId"
    fun readerRoute(stripId: String) = "reader/$stripId"
}

private data class TopTabDestination(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val topTabs = listOf(
    TopTabDestination(
        route = StripDeckRoutes.WORLD_VAULT,
        labelRes = R.string.nav_world,
        selectedIcon = Icons.Filled.Public,
        unselectedIcon = Icons.Outlined.Public,
        testTag = "nav_tab_world"
    ),
    TopTabDestination(
        route = StripDeckRoutes.TODAY,
        labelRes = R.string.nav_today,
        selectedIcon = Icons.Filled.Today,
        unselectedIcon = Icons.Outlined.Today,
        testTag = "nav_tab_today"
    ),
    TopTabDestination(
        route = StripDeckRoutes.EXPLORE,
        labelRes = R.string.nav_explore,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore,
        testTag = "nav_tab_explore"
    ),
    TopTabDestination(
        route = StripDeckRoutes.RANDOM_LAB,
        labelRes = R.string.nav_random,
        selectedIcon = Icons.Filled.Casino,
        unselectedIcon = Icons.Outlined.Casino,
        testTag = "nav_tab_random"
    ),
    TopTabDestination(
        route = StripDeckRoutes.SAVED,
        labelRes = R.string.nav_saved,
        selectedIcon = Icons.Filled.Bookmarks,
        unselectedIcon = Icons.Outlined.Bookmarks,
        testTag = "nav_tab_saved"
    )
)

@Composable
fun StripDeckApp(
    viewModel: ComicViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isTopLevelRoute = topTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    fun navigateTopTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.statusBars,
            bottomBar = {
                if (isTopLevelRoute && !useNavigationRail) {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        topTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            val label = stringResource(tab.labelRes)
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateTopTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isTopLevelRoute && useNavigationRail) {
                    NavigationRail(
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        topTabs.forEach { tab ->
                            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                            val label = stringResource(tab.labelRes)
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopTab(tab.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = label
                                    )
                                },
                                label = { Text(label) },
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    NavHost(
                        navController = navController,
                        startDestination = StripDeckRoutes.WORLD_VAULT
                    ) {
                        composable(StripDeckRoutes.TODAY) {
                            TodayScreen(
                                uiState = uiState,
                                onQueryChange = viewModel::updateSearchQuery,
                                onSelectGenre = viewModel::selectGenre,
                                onClearFilters = viewModel::clearSearchAndGenre,
                                onOpenExploreSearch = { navigateTopTab(StripDeckRoutes.EXPLORE) },
                                onOpenSeries = { seriesId ->
                                    navController.navigate(StripDeckRoutes.seriesRoute(seriesId))
                                },
                                onOpenStrip = { stripId ->
                                    navController.navigate(StripDeckRoutes.readerRoute(stripId))
                                },
                                onRandomAnyStrip = {
                                    val strip = viewModel.rollRandomStripForNavigation()
                                    navController.navigate(StripDeckRoutes.readerRoute(strip.id))
                                },
                                onRandomSeries = {
                                    val series = viewModel.rollRandomSeries()
                                    navController.navigate(StripDeckRoutes.seriesRoute(series.id))
                                },
                                onRandomFilteredSeries = {
                                    val picked = viewModel.rollRandomSeries(fromFiltered = true)
                                    navController.navigate(StripDeckRoutes.seriesRoute(picked.id))
                                },
                                onRandomByGenre = { genre ->
                                    val strip = viewModel.rollRandomStripForNavigation(genre = genre)
                                    navController.navigate(StripDeckRoutes.readerRoute(strip.id))
                                },
                                onOpenRandomLabXkcd = {
                                    viewModel.rollRandomLiveXkcd()
                                    navigateTopTab(StripDeckRoutes.RANDOM_LAB)
                                },
                                onOpenUpcomingWorld = {
                                    viewModel.selectWorldSource(WorldSource.UPCOMING)
                                    navigateTopTab(StripDeckRoutes.WORLD_VAULT)
                                },
                                onRollFeaturedRandom = {
                                    viewModel.rollFeaturedStripRandom()
                                },
                                onShuffleMix = {
                                    viewModel.shuffleTodayMix()
                                },
                                onToggleFavoriteSeries = viewModel::toggleFavoriteSeries,
                                onToggleBookmarkStrip = { stripId ->
                                    viewModel.toggleBookmarkStrip(stripId)
                                }
                            )
                        }

                        composable(StripDeckRoutes.EXPLORE) {
                            ExploreScreen(
                                uiState = uiState,
                                onQueryChange = viewModel::updateSearchQuery,
                                onSelectGenre = viewModel::selectGenre,
                                onSelectSort = viewModel::selectSortOption,
                                onClearFilters = viewModel::clearSearchAndGenre,
                                onPickRandomMatch = {
                                    val picked = viewModel.rollRandomSeries(fromFiltered = true)
                                    navController.navigate(StripDeckRoutes.seriesRoute(picked.id))
                                },
                                onOpenSeries = { seriesId ->
                                    navController.navigate(StripDeckRoutes.seriesRoute(seriesId))
                                },
                                onOpenStrip = { stripId ->
                                    navController.navigate(StripDeckRoutes.readerRoute(stripId))
                                },
                                onToggleFavoriteSeries = viewModel::toggleFavoriteSeries,
                                onToggleBookmarkStrip = viewModel::toggleBookmarkStrip
                            )
                        }

                        composable(StripDeckRoutes.WORLD_VAULT) {
                            WorldVaultScreen(
                                uiState = uiState,
                                onQueryChange = viewModel::updateWorldQuery,
                                onSearchSubmit = viewModel::loadWorldComics,
                                onSelectSource = viewModel::selectWorldSource,
                                onRollRandomWorldBook = viewModel::rollRandomWorldBookComic,
                                onRollRandomUpcoming = viewModel::rollRandomUpcomingWorldComic,
                                onRollRandomWorldManga = viewModel::rollRandomWorldManga,
                                onRollRandomPublicDomain = viewModel::rollRandomPublicDomainComic,
                                onToggleSaveWorldComic = viewModel::toggleSaveWorldComic,
                                onSaveWorldComicWithStatus = viewModel::saveWorldComicWithStatus,
                                onOpenSeries = { seriesId ->
                                    navController.navigate(StripDeckRoutes.seriesRoute(seriesId))
                                },
                                onLoadMore = viewModel::loadMoreWorldComics,
                                onRetryLoad = viewModel::loadWorldComics
                            )
                        }

                        composable(StripDeckRoutes.RANDOM_LAB) {
                            RandomLabScreen(
                                uiState = uiState,
                                onSpinRandomAny = { viewModel.rollLabRandomStrip() },
                                onSpinRandomByMood = { mood -> viewModel.rollLabRandomStrip(mood = mood) },
                                onSpinRandomByGenre = { genre -> viewModel.rollLabRandomStrip(genre = genre) },
                                onRollRandomXkcd = { viewModel.rollRandomLiveXkcd() },
                                onToggleXkcdAlt = { viewModel.toggleXkcdAltText() },
                                onOpenStrip = { stripId ->
                                    navController.navigate(StripDeckRoutes.readerRoute(stripId))
                                },
                                onOpenSeries = { seriesId ->
                                    navController.navigate(StripDeckRoutes.seriesRoute(seriesId))
                                }
                            )
                        }

                        composable(StripDeckRoutes.SAVED) {
                            SavedScreen(
                                uiState = uiState,
                                onOpenSeries = { seriesId ->
                                    navController.navigate(StripDeckRoutes.seriesRoute(seriesId))
                                },
                                onOpenStrip = { stripId ->
                                    navController.navigate(StripDeckRoutes.readerRoute(stripId))
                                },
                                onToggleFavoriteSeries = viewModel::toggleFavoriteSeries,
                                onToggleBookmarkStrip = viewModel::toggleBookmarkStrip,
                                onUpdateWorldComicStatus = viewModel::updateSavedWorldComicStatus,
                                onRemoveWorldComic = viewModel::removeSavedWorldComic,
                                onOpenWorldHub = { navigateTopTab(StripDeckRoutes.WORLD_VAULT) },
                                onRandomFromSaved = {
                                    val picked = viewModel.rollRandomFromSaved()
                                    if (picked != null) {
                                        navController.navigate(StripDeckRoutes.readerRoute(picked.id))
                                    }
                                },
                                onDiscoverRandomAny = {
                                    val strip = viewModel.rollRandomStripForNavigation()
                                    navController.navigate(StripDeckRoutes.readerRoute(strip.id))
                                }
                            )
                        }

                        composable(
                            route = StripDeckRoutes.SERIES_DETAIL,
                            arguments = listOf(navArgument("seriesId") { type = NavType.StringType })
                        ) { entry ->
                            val seriesId = entry.arguments?.getString("seriesId").orEmpty()
                            val series = viewModel.getSeriesById(seriesId)
                            if (series != null) {
                                SeriesDetailScreen(
                                    series = series,
                                    isFavorite = series.id in uiState.preferences.favoriteSeriesIds,
                                    bookmarkedStripIds = uiState.preferences.bookmarkedStripIds,
                                    onBack = { navController.popBackStack() },
                                    onToggleFavorite = { viewModel.toggleFavoriteSeries(series.id) },
                                    onOpenStrip = { stripId ->
                                        navController.navigate(StripDeckRoutes.readerRoute(stripId))
                                    },
                                    onRandomStripInSeries = {
                                        val randomStrip = viewModel.rollRandomStripInSeries(series.id)
                                        if (randomStrip != null) {
                                            navController.navigate(StripDeckRoutes.readerRoute(randomStrip.id))
                                        }
                                    },
                                    onToggleBookmarkStrip = viewModel::toggleBookmarkStrip
                                )
                            }
                        }

                        composable(
                            route = StripDeckRoutes.STRIP_READER,
                            arguments = listOf(navArgument("stripId") { type = NavType.StringType })
                        ) { entry ->
                            val stripId = entry.arguments?.getString("stripId").orEmpty()
                            val strip = viewModel.getStripById(stripId)
                            val series = viewModel.getSeriesForStrip(stripId)
                            if (strip != null) {
                                val prevStrip = viewModel.getAdjacentStrip(stripId, -1)
                                val nextStrip = viewModel.getAdjacentStrip(stripId, 1)
                                StripReaderScreen(
                                    strip = strip,
                                    series = series,
                                    hasPrev = prevStrip != null,
                                    hasNext = nextStrip != null,
                                    isBookmarked = strip.id in uiState.preferences.bookmarkedStripIds,
                                    onBack = { navController.popBackStack() },
                                    onOpenSeries = { sId ->
                                        navController.navigate(StripDeckRoutes.seriesRoute(sId))
                                    },
                                    onPrevStrip = {
                                        if (prevStrip != null) {
                                            navController.navigate(StripDeckRoutes.readerRoute(prevStrip.id)) {
                                                popUpTo(StripDeckRoutes.STRIP_READER) { inclusive = true }
                                            }
                                        }
                                    },
                                    onNextStrip = {
                                        if (nextStrip != null) {
                                            navController.navigate(StripDeckRoutes.readerRoute(nextStrip.id)) {
                                                popUpTo(StripDeckRoutes.STRIP_READER) { inclusive = true }
                                            }
                                        }
                                    },
                                    onRandomInSeries = {
                                        val random = series?.let {
                                            viewModel.rollRandomStripInSeries(it.id, excludeId = strip.id)
                                        }
                                        if (random != null && random.id != strip.id) {
                                            navController.navigate(StripDeckRoutes.readerRoute(random.id)) {
                                                popUpTo(StripDeckRoutes.STRIP_READER) { inclusive = true }
                                            }
                                        }
                                    },
                                    onRandomAnyStrip = {
                                        val random = viewModel.rollRandomStripForNavigation(excludeId = strip.id)
                                        navController.navigate(StripDeckRoutes.readerRoute(random.id)) {
                                            popUpTo(StripDeckRoutes.STRIP_READER) { inclusive = true }
                                        }
                                    },
                                    onToggleBookmark = { viewModel.toggleBookmarkStrip(strip.id) },
                                    onMarkRead = { viewModel.markStripRead(strip.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
