package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ComicRepository
import com.example.data.UserComicPreferences
import com.example.data.UserPreferencesRepository
import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.model.ComicStrip
import com.example.model.ExploreSortOption
import com.example.model.LibraryShelfStatus
import com.example.model.LiveWebcomicStrip
import com.example.model.MoodTag
import com.example.model.UpcomingStripRelease
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface LiveXkcdUiState {
    data object Idle : LiveXkcdUiState
    data object Loading : LiveXkcdUiState
    data class Success(val comic: LiveWebcomicStrip) : LiveXkcdUiState
    data class Error(val message: String) : LiveXkcdUiState
}

sealed interface WorldVaultUiState {
    data object Loading : WorldVaultUiState
    data class Success(val items: List<WorldComicItem>) : WorldVaultUiState
    data class Error(val message: String) : WorldVaultUiState
}

data class ComicUiState(
    val allSeries: List<ComicSeries> = emptyList(),
    val featuredStrip: ComicStrip? = null,
    val randomMixStrips: List<ComicStrip> = emptyList(),
    val labActiveStrip: ComicStrip? = null,
    val labRollTick: Int = 0,
    val searchQuery: String = "",
    val selectedGenre: ComicGenre? = null,
    val sortOption: ExploreSortOption = ExploreSortOption.POPULAR,
    val filteredSeries: List<ComicSeries> = emptyList(),
    val filteredStrips: List<ComicStrip> = emptyList(),
    val preferences: UserComicPreferences = UserComicPreferences(),
    val liveXkcdState: LiveXkcdUiState = LiveXkcdUiState.Idle,
    val showXkcdAltText: Boolean = false,
    val worldQuery: String = "",
    val worldSourceFilter: WorldSource? = null,
    val worldPage: Int = 1,
    val isLoadingMoreWorld: Boolean = false,
    val worldVaultState: WorldVaultUiState = WorldVaultUiState.Loading,
    val worldSpotlight: WorldComicItem? = null,
    val isWorldRandomLoading: Boolean = false,
    val upcomingStrips: List<UpcomingStripRelease> = emptyList()
)

class ComicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ComicRepository()
    private val prefsRepository = UserPreferencesRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(
        ComicUiState(
            allSeries = repository.allSeries,
            featuredStrip = repository.getFeaturedTodayStrip(),
            randomMixStrips = repository.pickRandomMix(3),
            labActiveStrip = repository.pickRandomStrip(),
            filteredSeries = repository.allSeries,
            upcomingStrips = repository.getUpcomingStripSchedule()
        )
    )
    val uiState: StateFlow<ComicUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepository.preferencesFlow.collect { prefs ->
                _uiState.update { it.copy(preferences = prefs) }
            }
        }
        loadLatestLiveXkcd()
        loadWorldComics()
    }

    fun getSeriesById(seriesId: String): ComicSeries? = repository.getSeriesById(seriesId)

    fun getStripById(stripId: String): ComicStrip? = repository.getStripById(stripId)

    fun getSeriesForStrip(stripId: String): ComicSeries? = repository.getSeriesForStrip(stripId)

    fun getAdjacentStrip(stripId: String, offset: Int): ComicStrip? =
        repository.getAdjacentStrip(stripId, offset)

    fun updateSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredSeries = repository.filterSeries(query, current.selectedGenre, current.sortOption),
                filteredStrips = repository.filterStrips(query, current.selectedGenre)
            )
        }
    }

    fun selectGenre(genre: ComicGenre?) {
        _uiState.update { current ->
            current.copy(
                selectedGenre = genre,
                filteredSeries = repository.filterSeries(current.searchQuery, genre, current.sortOption),
                filteredStrips = repository.filterStrips(current.searchQuery, genre)
            )
        }
    }

    fun clearSearchAndGenre() {
        _uiState.update { current ->
            current.copy(
                searchQuery = "",
                selectedGenre = null,
                filteredSeries = repository.filterSeries("", null, current.sortOption),
                filteredStrips = emptyList()
            )
        }
    }

    fun selectSortOption(option: ExploreSortOption) {
        _uiState.update { current ->
            current.copy(
                sortOption = option,
                filteredSeries = repository.filterSeries(current.searchQuery, current.selectedGenre, option)
            )
        }
    }

    fun shuffleTodayMix() {
        val currentIds = _uiState.value.randomMixStrips.map { it.id }.toSet()
        _uiState.update {
            it.copy(randomMixStrips = repository.pickRandomMix(3, excludeIds = currentIds))
        }
    }

    fun rollFeaturedStripRandom() {
        val currentId = _uiState.value.featuredStrip?.id
        val next = repository.pickRandomStrip(excludeId = currentId)
        _uiState.update { it.copy(featuredStrip = next) }
        recordRandomRoll(next.id)
    }

    fun rollRandomStripForNavigation(
        genre: ComicGenre? = null,
        mood: MoodTag? = null,
        excludeId: String? = null
    ): ComicStrip {
        val picked = when {
            genre != null -> repository.pickRandomStripByGenre(genre, excludeId)
            mood != null -> repository.pickRandomStripByMood(mood, excludeId)
            else -> repository.pickRandomStrip(excludeId)
        }
        recordRandomRoll(picked.id)
        return picked
    }

    fun rollRandomStripInSeries(seriesId: String, excludeId: String? = null): ComicStrip? {
        val picked = repository.pickRandomStripInSeries(seriesId, excludeId) ?: return null
        recordRandomRoll(picked.id)
        return picked
    }

    fun rollRandomSeries(fromFiltered: Boolean = false, excludeId: String? = null): ComicSeries {
        val pool = if (fromFiltered) _uiState.value.filteredSeries else _uiState.value.allSeries
        return repository.pickRandomSeries(pool, excludeId)
    }

    fun rollLabRandomStrip(mood: MoodTag? = null, genre: ComicGenre? = null) {
        val currentId = _uiState.value.labActiveStrip?.id
        val picked = when {
            mood != null -> repository.pickRandomStripByMood(mood, currentId)
            genre != null -> repository.pickRandomStripByGenre(genre, currentId)
            else -> repository.pickRandomStrip(currentId)
        }
        _uiState.update {
            it.copy(
                labActiveStrip = picked,
                labRollTick = it.labRollTick + 1
            )
        }
        recordRandomRoll(picked.id)
    }

    fun rollRandomFromSaved(): ComicStrip? {
        val prefs = _uiState.value.preferences
        val savedStripPool = mutableListOf<ComicStrip>()
        prefs.bookmarkedStripIds.mapNotNullTo(savedStripPool) { repository.getStripById(it) }
        prefs.favoriteSeriesIds.forEach { seriesId ->
            repository.getSeriesById(seriesId)?.strips?.let { savedStripPool.addAll(it) }
        }
        if (savedStripPool.isEmpty()) return null
        val distinct = savedStripPool.distinctBy { it.id }
        val picked = distinct.random()
        recordRandomRoll(picked.id)
        return picked
    }

    fun toggleFavoriteSeries(seriesId: String) {
        viewModelScope.launch {
            prefsRepository.toggleFavoriteSeries(seriesId)
        }
    }

    fun toggleBookmarkStrip(stripId: String) {
        viewModelScope.launch {
            prefsRepository.toggleBookmarkStrip(stripId)
        }
    }

    fun markStripRead(stripId: String) {
        viewModelScope.launch {
            prefsRepository.markStripRead(stripId)
        }
    }

    private fun recordRandomRoll(stripId: String) {
        viewModelScope.launch {
            prefsRepository.recordRandomRoll(stripId)
        }
    }

    fun loadLatestLiveXkcd() {
        viewModelScope.launch {
            _uiState.update { it.copy(liveXkcdState = LiveXkcdUiState.Loading, showXkcdAltText = false) }
            val result = repository.fetchLatestLiveXkcd()
            _uiState.update { state ->
                state.copy(
                    liveXkcdState = result.fold(
                        onSuccess = { LiveXkcdUiState.Success(it) },
                        onFailure = { LiveXkcdUiState.Error(it.localizedMessage ?: "Offline or unavailable") }
                    )
                )
            }
        }
    }

    fun rollRandomLiveXkcd() {
        val currentNum = (_uiState.value.liveXkcdState as? LiveXkcdUiState.Success)?.comic?.number
        viewModelScope.launch {
            _uiState.update { it.copy(liveXkcdState = LiveXkcdUiState.Loading, showXkcdAltText = false) }
            val result = repository.fetchRandomLiveXkcd(excludeNum = currentNum)
            _uiState.update { state ->
                state.copy(
                    liveXkcdState = result.fold(
                        onSuccess = { LiveXkcdUiState.Success(it) },
                        onFailure = { LiveXkcdUiState.Error(it.localizedMessage ?: "Check connection to fetch live XKCD") }
                    )
                )
            }
        }
    }

    fun toggleXkcdAltText() {
        _uiState.update { it.copy(showXkcdAltText = !it.showXkcdAltText) }
    }

    fun updateWorldQuery(query: String) {
        _uiState.update { it.copy(worldQuery = query) }
    }

    fun selectWorldSource(source: WorldSource?) {
        _uiState.update { it.copy(worldSourceFilter = source, worldPage = 1) }
        loadWorldComics()
    }

    fun loadWorldComics() {
        val q = _uiState.value.worldQuery
        val src = _uiState.value.worldSourceFilter
        viewModelScope.launch {
            _uiState.update { it.copy(worldVaultState = WorldVaultUiState.Loading, worldPage = 1) }
            val result = repository.fetchWorldComics(q, src, page = 1)
            _uiState.update { state ->
                state.copy(
                    worldVaultState = result.fold(
                        onSuccess = { WorldVaultUiState.Success(it) },
                        onFailure = { WorldVaultUiState.Error(it.localizedMessage ?: "Unable to reach world archives") }
                    )
                )
            }
        }
    }

    fun loadMoreWorldComics() {
        val currentState = _uiState.value
        if (currentState.isLoadingMoreWorld) return
        val existingItems = (currentState.worldVaultState as? WorldVaultUiState.Success)?.items ?: return
        val nextPage = currentState.worldPage + 1
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMoreWorld = true) }
            val result = repository.fetchWorldComics(
                query = currentState.worldQuery,
                sourceFilter = currentState.worldSourceFilter,
                page = nextPage
            )
            _uiState.update { state ->
                val newItems = result.getOrNull().orEmpty()
                val merged = (existingItems + newItems).distinctBy { it.id }
                state.copy(
                    isLoadingMoreWorld = false,
                    worldPage = if (newItems.isNotEmpty()) nextPage else state.worldPage,
                    worldVaultState = WorldVaultUiState.Success(merged)
                )
            }
        }
    }

    fun rollRandomWorldBookComic() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorldRandomLoading = true) }
            val result = repository.fetchRandomWorldBookComic()
            _uiState.update { state ->
                state.copy(
                    isWorldRandomLoading = false,
                    worldSpotlight = result.getOrNull() ?: state.worldSpotlight
                )
            }
        }
    }

    fun rollRandomWorldManga() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorldRandomLoading = true) }
            val result = repository.fetchRandomWorldManga()
            _uiState.update { state ->
                state.copy(
                    isWorldRandomLoading = false,
                    worldSpotlight = result.getOrNull() ?: state.worldSpotlight
                )
            }
        }
    }

    fun rollRandomPublicDomainComic() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorldRandomLoading = true) }
            val result = repository.fetchRandomPublicDomainComic()
            _uiState.update { state ->
                state.copy(
                    isWorldRandomLoading = false,
                    worldSpotlight = result.getOrNull() ?: state.worldSpotlight
                )
            }
        }
    }

    fun rollRandomUpcomingWorldComic() {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorldRandomLoading = true) }
            val result = repository.fetchRandomUpcomingWorldComic()
            _uiState.update { state ->
                state.copy(
                    isWorldRandomLoading = false,
                    worldSpotlight = result.getOrNull() ?: state.worldSpotlight
                )
            }
        }
    }

    fun toggleSaveWorldComic(item: WorldComicItem) {
        val isAlreadySaved = _uiState.value.preferences.savedWorldComics.any { it.id == item.id }
        viewModelScope.launch {
            if (isAlreadySaved) {
                prefsRepository.removeSavedWorldComic(item.id)
            } else {
                prefsRepository.saveOrUpdateWorldComic(item, LibraryShelfStatus.COLLECTED)
            }
        }
    }

    fun saveWorldComicWithStatus(item: WorldComicItem, status: LibraryShelfStatus) {
        viewModelScope.launch {
            prefsRepository.saveOrUpdateWorldComic(item, status)
        }
    }

    fun updateSavedWorldComicStatus(id: String, status: LibraryShelfStatus) {
        viewModelScope.launch {
            prefsRepository.updateWorldComicShelfStatus(id, status)
        }
    }

    fun removeSavedWorldComic(id: String) {
        viewModelScope.launch {
            prefsRepository.removeSavedWorldComic(id)
        }
    }
}
