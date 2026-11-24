package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.LibraryShelfStatus
import com.example.model.SavedWorldComic
import com.example.model.SavedWorldComicLibrary
import com.example.model.WorldComicItem
import com.example.model.toSavedWorldComic
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.stripDeckDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "strip_deck_prefs"
)

data class UserComicPreferences(
    val favoriteSeriesIds: Set<String> = emptySet(),
    val bookmarkedStripIds: Set<String> = emptySet(),
    val readStripIds: Set<String> = emptySet(),
    val randomRollCount: Int = 0,
    val recentRandomIds: List<String> = emptyList(),
    val savedWorldComics: List<SavedWorldComic> = emptyList()
)

class UserPreferencesRepository(private val context: Context) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val libraryAdapter = moshi.adapter(SavedWorldComicLibrary::class.java)

    private object Keys {
        val FAVORITE_SERIES = stringSetPreferencesKey("favorite_series_ids")
        val BOOKMARKED_STRIPS = stringSetPreferencesKey("bookmarked_strip_ids")
        val READ_STRIPS = stringSetPreferencesKey("read_strip_ids")
        val RANDOM_ROLL_COUNT = intPreferencesKey("random_roll_count")
        val RECENT_RANDOM_IDS = stringPreferencesKey("recent_random_ids")
        val SAVED_WORLD_COMICS_JSON = stringPreferencesKey("saved_world_comics_json")
    }

    val preferencesFlow: Flow<UserComicPreferences> = context.stripDeckDataStore.data.map { prefs ->
        val recentRaw = prefs[Keys.RECENT_RANDOM_IDS].orEmpty()
        val recentList = if (recentRaw.isBlank()) emptyList() else recentRaw.split(",").filter { it.isNotBlank() }
        val savedJson = prefs[Keys.SAVED_WORLD_COMICS_JSON].orEmpty()
        val savedList = if (savedJson.isBlank()) {
            emptyList()
        } else {
            runCatching { libraryAdapter.fromJson(savedJson)?.items.orEmpty() }.getOrDefault(emptyList())
        }
        UserComicPreferences(
            favoriteSeriesIds = prefs[Keys.FAVORITE_SERIES] ?: emptySet(),
            bookmarkedStripIds = prefs[Keys.BOOKMARKED_STRIPS] ?: emptySet(),
            readStripIds = prefs[Keys.READ_STRIPS] ?: emptySet(),
            randomRollCount = prefs[Keys.RANDOM_ROLL_COUNT] ?: 0,
            recentRandomIds = recentList,
            savedWorldComics = savedList.sortedByDescending { it.addedAtEpochMs }
        )
    }

    suspend fun toggleFavoriteSeries(seriesId: String) {
        context.stripDeckDataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITE_SERIES] ?: emptySet()
            prefs[Keys.FAVORITE_SERIES] = if (seriesId in current) current - seriesId else current + seriesId
        }
    }

    suspend fun toggleBookmarkStrip(stripId: String) {
        context.stripDeckDataStore.edit { prefs ->
            val current = prefs[Keys.BOOKMARKED_STRIPS] ?: emptySet()
            prefs[Keys.BOOKMARKED_STRIPS] = if (stripId in current) current - stripId else current + stripId
        }
    }

    suspend fun markStripRead(stripId: String) {
        context.stripDeckDataStore.edit { prefs ->
            val current = prefs[Keys.READ_STRIPS] ?: emptySet()
            if (stripId !in current) {
                prefs[Keys.READ_STRIPS] = current + stripId
            }
        }
    }

    suspend fun recordRandomRoll(stripId: String) {
        context.stripDeckDataStore.edit { prefs ->
            val count = prefs[Keys.RANDOM_ROLL_COUNT] ?: 0
            prefs[Keys.RANDOM_ROLL_COUNT] = count + 1

            val existing = prefs[Keys.RECENT_RANDOM_IDS].orEmpty()
                .split(",")
                .filter { it.isNotBlank() && it != stripId }
            val updated = (listOf(stripId) + existing).take(12)
            prefs[Keys.RECENT_RANDOM_IDS] = updated.joinToString(",")
        }
    }

    suspend fun saveOrUpdateWorldComic(
        item: WorldComicItem,
        status: LibraryShelfStatus = LibraryShelfStatus.COLLECTED
    ) {
        context.stripDeckDataStore.edit { prefs ->
            val current = decodeLibrary(prefs[Keys.SAVED_WORLD_COMICS_JSON])
            val existing = current.find { it.id == item.id }
            val entry = item.toSavedWorldComic(
                shelfStatus = status,
                addedAtEpochMs = existing?.addedAtEpochMs ?: System.currentTimeMillis()
            )
            val updated = listOf(entry) + current.filterNot { it.id == item.id }
            prefs[Keys.SAVED_WORLD_COMICS_JSON] = libraryAdapter.toJson(SavedWorldComicLibrary(updated))
        }
    }

    suspend fun updateWorldComicShelfStatus(id: String, status: LibraryShelfStatus) {
        context.stripDeckDataStore.edit { prefs ->
            val current = decodeLibrary(prefs[Keys.SAVED_WORLD_COMICS_JSON])
            val updated = current.map { entry ->
                if (entry.id == id) entry.copy(shelfStatusName = status.name) else entry
            }
            prefs[Keys.SAVED_WORLD_COMICS_JSON] = libraryAdapter.toJson(SavedWorldComicLibrary(updated))
        }
    }

    suspend fun removeSavedWorldComic(id: String) {
        context.stripDeckDataStore.edit { prefs ->
            val current = decodeLibrary(prefs[Keys.SAVED_WORLD_COMICS_JSON])
            val updated = current.filterNot { it.id == id }
            prefs[Keys.SAVED_WORLD_COMICS_JSON] = libraryAdapter.toJson(SavedWorldComicLibrary(updated))
        }
    }

    private fun decodeLibrary(json: String?): List<SavedWorldComic> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching { libraryAdapter.fromJson(json)?.items.orEmpty() }.getOrDefault(emptyList())
    }
}
