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
            issueLabel = "Vol. 20",
            genres = listOf("Adventure"),
            synopsis = "Classic European comic.",
            coverUrl = null,
            statsLabel = "4.8",
            externalReadUrl = "https://openlibrary.org/works/OL1W"
        )
        val saved = sampleWorld.toSavedWorldComic(LibraryShelfStatus.READING)
        assertEquals(LibraryShelfStatus.READING, saved.resolveShelfStatus())
        assertEquals("Pull List", LibraryShelfStatus.READING.label)
        assertEquals("Collection", LibraryShelfStatus.COLLECTED.label)
        assertEquals(3, com.example.model.HomeSpotlightTab.entries.size)
        assertEquals(WorldSource.WORLD_CATALOG, saved.resolveSource())
        assertEquals(sampleWorld.title, saved.toWorldComicItem().title)
        assertEquals("Vol. 20", saved.toWorldComicItem().issueLabel)

        val calendar = repo.getChronologicalIssueCalendar()
        assertTrue(calendar.size == repo.allStrips.size + repo.getUpcomingStripSchedule().size)
        assertTrue(calendar.first().isUpcoming)
        assertTrue(calendar.first().sortKey > calendar.last().sortKey)

        assertEquals("Issue #12", repo.extractIssueOrVolumeLabel("Action Comics #12 (1939)"))
        assertEquals("Issue #5", repo.extractIssueOrVolumeLabel("Chacha Chaudhary Issue 5"))
        assertEquals("Vol. 3", repo.extractIssueOrVolumeLabel("Batman Vol. 3: Year One"))
        assertEquals("Issue #42", repo.extractIssueOrVolumeLabel("Classic Archive", explicitIssue = "42"))

        val stripByIssue = repo.filterStrips("#1042", null)
        assertTrue(stripByIssue.any { it.stripNumber == 1042 })
        val stripByDate = repo.filterStrips("Nov 22", null)
        assertTrue(stripByDate.any { it.publishDate.contains("Nov 22") })

        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.example.data.FlexibleTextAdapter())
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
        val libAdapter = moshi.adapter(com.example.model.SavedWorldComicLibrary::class.java)
        val json = libAdapter.toJson(com.example.model.SavedWorldComicLibrary(listOf(saved)))
        val decoded = libAdapter.fromJson(json)
        assertEquals(1, decoded?.items?.size)
        assertEquals("Tintin in Tibet", decoded?.items?.first()?.title)
        assertEquals("Vol. 20", decoded?.items?.first()?.issueLabel)
    }

    @Test
    fun comicVineMappingAndKeyValidationWork() {
        val placeholderRepo = ComicRepository(
            comicVineApiKeyProvider = { ComicRepository.COMICVINE_PLACEHOLDER }
        )
        assertEquals(false, placeholderRepo.isComicVineConfigured())

        val blankRepo = ComicRepository(comicVineApiKeyProvider = { "   " })
        assertEquals(false, blankRepo.isComicVineConfigured())

        val activeRepo = ComicRepository(comicVineApiKeyProvider = { "test_real_key_123" })
        assertEquals(true, activeRepo.isComicVineConfigured())

        val issueDto = com.example.data.ComicVineResourceDto(
            id = 987654,
            name = "The Dark Hunt",
            issueNumber = "1",
            storeDate = "2026-11-18",
            coverDate = "2027-01-01",
            description = "<p>A brand new <b>Gotham</b> chapter begins.</p>",
            resourceType = "issue",
            volume = com.example.data.ComicVineNamedRefDto(id = 456, name = "Absolute Batman"),
            publisher = com.example.data.ComicVineNamedRefDto(id = 10, name = "DC Comics"),
            image = com.example.data.ComicVineImageDto(
                mediumUrl = "https://comicvine.gamespot.com/a/uploads/scale_medium/sample.jpg"
            ),
            siteDetailUrl = "https://comicvine.gamespot.com/absolute-batman-1/4000-987654/"
        )

        val mappedIssue = with(activeRepo) { issueDto.toWorldComicItem() }!!
        assertEquals("cv_iss_987654", mappedIssue.id)
        assertEquals(WorldSource.COMICVINE, mappedIssue.source)
        assertEquals("Absolute Batman #1: The Dark Hunt", mappedIssue.title)
        assertEquals("Issue #1", mappedIssue.issueLabel)
        assertEquals("2026-11-18", mappedIssue.yearOrDate)
        assertEquals("DC Comics", mappedIssue.creator)
        assertEquals("A brand new Gotham chapter begins.", mappedIssue.synopsis)

        val volumeDto = com.example.data.ComicVineResourceDto(
            id = 555,
            name = "Ultimate Spider-Man",
            countOfIssues = 24,
            startYear = "2024",
            resourceType = "volume",
            publisher = com.example.data.ComicVineNamedRefDto(id = 31, name = "Marvel")
        )
        val mappedVolume = with(activeRepo) { volumeDto.toWorldComicItem() }!!
        assertEquals("cv_vol_555", mappedVolume.id)
        assertEquals("Ultimate Spider-Man", mappedVolume.title)
        assertEquals("24 Issues", mappedVolume.issueLabel)
        assertEquals("2024", mappedVolume.yearOrDate)
        assertEquals("Marvel", mappedVolume.creator)
    }
}

