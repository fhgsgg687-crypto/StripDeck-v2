package com.example

import com.example.data.ComicRepository
import com.example.model.ComicGenre
import com.example.model.ExploreSortOption
import com.example.model.LibraryShelfStatus
import com.example.model.MoodTag
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import com.example.model.toSavedWorldComic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun catalogAndRandomizersWork() {
        val repo = ComicRepository()
        assertTrue(repo.allSeries.size >= 12)
        assertTrue(repo.allStrips.size >= 25)

        val first = repo.pickRandomStrip()
        val second = repo.pickRandomStrip(excludeId = first.id)
        assertNotEquals(first.id, second.id)

        val sciFiStrip = repo.pickRandomStripByGenre(ComicGenre.SCI_FI)
        assertEquals(ComicGenre.SCI_FI, repo.getSeriesById(sciFiStrip.seriesId)?.genre)

        val moodStrip = repo.pickRandomStripByMood(MoodTag.QUICK_LAUGH)
        assertTrue(MoodTag.QUICK_LAUGH in moodStrip.moodTags)

        val mix = repo.pickRandomMix(3)
        assertEquals(3, mix.size)

        val filteredByTitle = repo.filterSeries("Marmalade", null, ExploreSortOption.POPULAR)
        assertEquals(1, filteredByTitle.size)
        assertEquals("marmalade_toast", filteredByTitle.first().id)

        val filteredByGenreQuery = repo.filterSeries("sci fi", null, ExploreSortOption.POPULAR)
        assertTrue(filteredByGenreQuery.isNotEmpty())
        assertTrue(filteredByGenreQuery.all { it.genre == ComicGenre.SCI_FI })

        val filteredStrips = repo.filterStrips("Gravity Audit", null)
        assertEquals(1, filteredStrips.size)
        assertEquals("mt_001", filteredStrips.first().id)

        val upcoming = repo.getUpcomingStripSchedule()
        assertTrue(upcoming.size >= 5)
        assertTrue(upcoming.all { it.releaseDate.contains("2026") && repo.getSeriesById(it.seriesId) != null })

        val sampleWorld = WorldComicItem(
            id = "ol_test_1",
            source = WorldSource.WORLD_CATALOG,
            title = "Tintin in Tibet",
            creator = "Herge",
            yearOrDate = "1960",
            genres = listOf("Adventure"),
            synopsis = "Classic European comic.",
            coverUrl = null,
            statsLabel = "4.8",
            externalReadUrl = "https://openlibrary.org/works/OL1W"
        )
        val saved = sampleWorld.toSavedWorldComic(LibraryShelfStatus.READING)
        assertEquals(LibraryShelfStatus.READING, saved.resolveShelfStatus())
        assertEquals(WorldSource.WORLD_CATALOG, saved.resolveSource())
        assertEquals(sampleWorld.title, saved.toWorldComicItem().title)

        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.example.data.FlexibleTextAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
        val libAdapter = moshi.adapter(com.example.model.SavedWorldComicLibrary::class.java)
        val json = libAdapter.toJson(com.example.model.SavedWorldComicLibrary(listOf(saved)))
        val decoded = libAdapter.fromJson(json)
        assertEquals(1, decoded?.items?.size)
        assertEquals("Tintin in Tibet", decoded?.items?.first()?.title)
    }
}
