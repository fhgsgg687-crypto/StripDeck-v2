package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ComicRepository
import com.example.model.ComicGenre
import com.example.model.ExploreSortOption
import com.example.model.MoodTag
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("StripDeck", appName)
    }

    @Test
    fun `catalog and randomizers work across genres and moods`() {
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

        val filtered = repo.filterSeries("Marmalade", null, ExploreSortOption.POPULAR)
        assertEquals(1, filtered.size)
        assertEquals("marmalade_toast", filtered.first().id)
    }
}
