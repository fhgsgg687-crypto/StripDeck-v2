package com.example.data

import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.model.ComicStrip
import com.example.model.ExploreSortOption
import com.example.model.LiveWebcomicStrip
import com.example.model.MoodTag
import com.example.model.UpcomingStripRelease
import com.example.model.WorldComicItem
import com.example.model.WorldSource
import com.example.model.XkcdComicDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.random.Random

class ComicRepository(
    private val xkcdApiService: XkcdApiService = XkcdApiService.create(),
    private val jikanApiService: JikanApiService = JikanApiService.create(),
    private val archiveApiService: InternetArchiveApiService = InternetArchiveApiService.create(),
    private val openLibraryApiService: OpenLibraryApiService = OpenLibraryApiService.create()
) {
    val allSeries: List<ComicSeries> = (ComicCatalogPart1.series + ComicCatalogPart2.series)
        .sortedBy { it.rank }

    val allStrips: List<ComicStrip> = allSeries.flatMap { it.strips }

    private val seriesById: Map<String, ComicSeries> = allSeries.associateBy { it.id }
    private val stripById: Map<String, ComicStrip> = allStrips.associateBy { it.id }

    private var cachedLatestXkcdNum: Int = 3015

    fun getSeriesById(seriesId: String): ComicSeries? = seriesById[seriesId]

    fun getStripById(stripId: String): ComicStrip? = stripById[stripId]

    fun getSeriesForStrip(stripId: String): ComicSeries? {
        val strip = stripById[stripId] ?: return null
        return seriesById[strip.seriesId]
    }

    fun getFeaturedTodayStrip(): ComicStrip {
        return stripById["mt_003"] ?: allStrips.first()
    }

    fun getUpcomingStripSchedule(): List<UpcomingStripRelease> = listOf(
        UpcomingStripRelease(
            id = "up_mt_1045",
            seriesId = "marmalade_toast",
            seriesTitle = "Marmalade & Toast",
            stripNumber = 1045,
            title = "Winter Blanket Takeover",
            releaseDate = "Nov 24, 2026",
            countdownLabel = "Tomorrow",
            isSundaySpecial = false,
            teaser = "Marmalade claims 98% of the heated blanket before 6 AM.",
            accentHex = 0xFFFF8F00
        ),
        UpcomingStripRelease(
            id = "up_cb_615",
            seriesId = "cosmos_bolt",
            seriesTitle = "Cosmos & Bolt",
            stripNumber = 615,
            title = "Pluto Fan Club",
            releaseDate = "Nov 26, 2026",
            countdownLabel = "In 3 Days",
            isSundaySpecial = false,
            teaser = "Leo and Bolt deliver a handmade planet certificate to Pluto.",
            accentHex = 0xFF1F6AF7
        ),
        UpcomingStripRelease(
            id = "up_gg_201",
            seriesId = "granny_glitch",
            seriesTitle = "Granny Glitch",
            stripNumber = 201,
            title = "Thanksgiving Co-Op",
            releaseDate = "Nov 29, 2026",
            countdownLabel = "Sunday Color",
            isSundaySpecial = true,
            teaser = "Grandma Bea hosts a 4-player arcade tournament after pie.",
            accentHex = 0xFFD81B60
        ),
        UpcomingStripRelease(
            id = "up_cc_411",
            seriesId = "cubicle_cactus",
            seriesTitle = "Cubicle Cactus",
            stripNumber = 411,
            title = "December Goals",
            releaseDate = "Dec 01, 2026",
            countdownLabel = "Next Week",
            isSundaySpecial = false,
            teaser = "Spike reviews Dev's Q4 resolutions over lukewarm coffee.",
            accentHex = 0xFF009688
        ),
        UpcomingStripRelease(
            id = "up_spz_289",
            seriesId = "star_pilot_zero",
            seriesTitle = "Star Pilot Zero",
            stripNumber = 289,
            title = "Comet Rush Hour",
            releaseDate = "Dec 04, 2026",
            countdownLabel = "Dec 2026",
            isSundaySpecial = false,
            teaser = "Nova attempts a shortcut through the Asteroid Belt.",
            accentHex = 0xFF1565C0
        ),
        UpcomingStripRelease(
            id = "up_cap_1422",
            seriesId = "captain_cardboard",
            seriesTitle = "Captain Cardboard",
            stripNumber = 1422,
            title = "The Ice Fortress",
            releaseDate = "Dec 06, 2026",
            countdownLabel = "Sunday Color",
            isSundaySpecial = true,
            teaser = "Pip builds an invincible snow-box citadel in the yard.",
            accentHex = 0xFFE53935
        )
    )

    fun filterSeries(
        query: String,
        genre: ComicGenre?,
        sortOption: ExploreSortOption
    ): List<ComicSeries> {
        val q = query.trim().lowercase()
        val filtered = allSeries.filter { series ->
            val matchesGenre = genre == null || series.genre == genre
            val genreKeywords = "${series.genre.label} ${series.genre.name.replace("_", " ")} ${series.genre.tagline}".lowercase()
            val matchesQuery = q.isEmpty() ||
                series.title.lowercase().contains(q) ||
                genreKeywords.contains(q) ||
                series.creator.lowercase().contains(q) ||
                series.synopsis.lowercase().contains(q) ||
                series.characters.any { it.name.lowercase().contains(q) } ||
                series.strips.any { it.title.lowercase().contains(q) }
            matchesGenre && matchesQuery
        }

        return when (sortOption) {
            ExploreSortOption.POPULAR -> filtered.sortedBy { it.rank }
            ExploreSortOption.A_TO_Z -> filtered.sortedBy { it.title }
            ExploreSortOption.NEWEST -> filtered.sortedByDescending { it.launchYear }
            ExploreSortOption.MOST_STRIPS -> filtered.sortedByDescending { it.strips.size }
        }
    }

    fun filterStrips(
        query: String,
        genre: ComicGenre?
    ): List<ComicStrip> {
        val q = query.trim().lowercase()
        if (q.isEmpty() && genre == null) return emptyList()
        return allStrips.filter { strip ->
            val series = seriesById[strip.seriesId]
            val matchesGenre = genre == null || series?.genre == genre
            val genreKeywords = series?.let {
                "${it.genre.label} ${it.genre.name.replace("_", " ")}".lowercase()
            }.orEmpty()
            val matchesQuery = q.isEmpty() ||
                strip.title.lowercase().contains(q) ||
                (series?.title?.lowercase()?.contains(q) == true) ||
                genreKeywords.contains(q)
            matchesGenre && matchesQuery
        }
    }

    fun pickRandomStrip(excludeId: String? = null): ComicStrip {
        val pool = allStrips.filter { it.id != excludeId }.ifEmpty { allStrips }
        return pool[Random.nextInt(pool.size)]
    }

    fun pickRandomStripByGenre(genre: ComicGenre, excludeId: String? = null): ComicStrip {
        val genreStrips = allSeries
            .filter { it.genre == genre }
            .flatMap { it.strips }
        val pool = genreStrips.filter { it.id != excludeId }.ifEmpty { genreStrips.ifEmpty { allStrips } }
        return pool[Random.nextInt(pool.size)]
    }

    fun pickRandomStripByMood(mood: MoodTag, excludeId: String? = null): ComicStrip {
        val moodStrips = allStrips.filter { mood in it.moodTags }
        val pool = moodStrips.filter { it.id != excludeId }.ifEmpty { moodStrips.ifEmpty { allStrips } }
        return pool[Random.nextInt(pool.size)]
    }

    fun pickRandomStripInSeries(seriesId: String, excludeId: String? = null): ComicStrip? {
        val strips = seriesById[seriesId]?.strips ?: return null
        val pool = strips.filter { it.id != excludeId }.ifEmpty { strips }
        return pool[Random.nextInt(pool.size)]
    }

    fun pickRandomSeries(
        candidates: List<ComicSeries> = allSeries,
        excludeId: String? = null
    ): ComicSeries {
        val source = candidates.ifEmpty { allSeries }
        val pool = source.filter { it.id != excludeId }.ifEmpty { source }
        return pool[Random.nextInt(pool.size)]
    }

    fun pickRandomMix(count: Int = 3, excludeIds: Set<String> = emptySet()): List<ComicStrip> {
        val pool = allStrips.filter { it.id !in excludeIds }.ifEmpty { allStrips }
        return pool.shuffled().take(count.coerceAtMost(pool.size))
    }

    fun getAdjacentStrip(stripId: String, offset: Int): ComicStrip? {
        val current = stripById[stripId] ?: return null
        val seriesStrips = seriesById[current.seriesId]?.strips ?: return null
        val idx = seriesStrips.indexOfFirst { it.id == stripId }
        if (idx == -1) return null
        val target = idx + offset
        return seriesStrips.getOrNull(target)
    }

    suspend fun fetchLatestLiveXkcd(): Result<LiveWebcomicStrip> = runCatching {
        val dto = xkcdApiService.getLatestComic()
        cachedLatestXkcdNum = dto.num.coerceAtLeast(1)
        dto.toLiveWebcomic()
    }

    suspend fun fetchRandomLiveXkcd(excludeNum: Int? = null): Result<LiveWebcomicStrip> = runCatching {
        val maxNum = cachedLatestXkcdNum.coerceAtLeast(2900)
        var randomNum: Int
        do {
            randomNum = Random.nextInt(1, maxNum + 1)
        } while (randomNum == 404 || randomNum == excludeNum)

        val dto = xkcdApiService.getComicByNumber(randomNum)
        dto.toLiveWebcomic()
    }

    private fun XkcdComicDto.toLiveWebcomic(): LiveWebcomicStrip {
        return LiveWebcomicStrip(
            number = num,
            title = safeTitle?.takeIf { it.isNotBlank() } ?: title,
            imageUrl = img,
            altText = alt,
            publishDate = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
        )
    }

    suspend fun fetchWorldComics(
        query: String,
        sourceFilter: WorldSource? = null,
        page: Int = 1
    ): Result<List<WorldComicItem>> = coroutineScope {
        val cleanQuery = query.trim()

        if (sourceFilter == WorldSource.UPCOMING) {
            val upcomingMangaDeferred = async {
                runCatching {
                    jikanApiService.searchManga(
                        query = cleanQuery.ifBlank { null },
                        status = "upcoming",
                        sfw = true,
                        orderBy = "popularity",
                        sort = "asc",
                        page = page,
                        limit = 15
                    ).data.map { it.toWorldComicItem(WorldSource.UPCOMING, isUpcoming = true) }
                }
            }
            val upcomingBooksDeferred = async {
                runCatching {
                    val baseQ = cleanQuery.ifBlank { "comics" }
                    openLibraryApiService.searchGlobalComics(
                        query = "$baseQ first_publish_year:[2026 TO 2028]",
                        subject = "comics",
                        sort = "new",
                        limit = 15,
                        page = page
                    ).docs.mapNotNull { it.toWorldComicItem(overrideSource = WorldSource.UPCOMING) }
                }
            }

            val upManga = upcomingMangaDeferred.await()
            val upBooks = upcomingBooksDeferred.await()
            val combinedUpcoming = buildList {
                val mList = upManga.getOrNull().orEmpty()
                val bList = upBooks.getOrNull().orEmpty()
                val maxSize = maxOf(mList.size, bList.size)
                for (i in 0 until maxSize) {
                    if (i < bList.size) add(bList[i])
                    if (i < mList.size) add(mList[i])
                }
            }.distinctBy { it.id }

            return@coroutineScope if (combinedUpcoming.isNotEmpty()) {
                Result.success(combinedUpcoming)
            } else {
                val err = upManga.exceptionOrNull() ?: upBooks.exceptionOrNull()
                if (err != null) Result.failure(err) else Result.success(emptyList())
            }
        }

        val fetchCatalog = sourceFilter == null || sourceFilter == WorldSource.WORLD_CATALOG
        val fetchArchive = sourceFilter == null || sourceFilter == WorldSource.PUBLIC_DOMAIN
        val jikanType = when (sourceFilter) {
            WorldSource.MANGA -> "manga"
            WorldSource.MANHWA -> "manhwa"
            WorldSource.MANHUA -> "manhua"
            else -> null
        }
        val fetchJikan = sourceFilter == null || jikanType != null

        val catalogDeferred = if (fetchCatalog) {
            async {
                runCatching {
                    val olQuery = cleanQuery.ifBlank { "comics OR graphic novel OR strip" }
                    openLibraryApiService.searchGlobalComics(
                        query = olQuery,
                        subject = "comics",
                        sort = if (cleanQuery.isBlank()) "rating" else null,
                        limit = 15,
                        page = page
                    ).docs.mapNotNull { it.toWorldComicItem() }
                }
            }
        } else null

        val archiveDeferred = if (fetchArchive) {
            async {
                runCatching {
                    val qExpr = if (cleanQuery.isBlank()) {
                        "collection:(comics) AND mediatype:(texts)"
                    } else {
                        val safeTerm = cleanQuery.replace("\"", "").replace("(", "").replace(")", "")
                        "collection:(comics) AND mediatype:(texts) AND (title:($safeTerm) OR subject:($safeTerm) OR creator:($safeTerm))"
                    }
                    archiveApiService.searchPublicDomainComics(query = qExpr, rows = 15, page = page)
                        .response?.docs.orEmpty().mapNotNull { it.toWorldComicItem() }
                }
            }
        } else null

        val mangaDeferred = if (fetchJikan) {
            async {
                runCatching {
                    jikanApiService.searchManga(
                        query = cleanQuery.ifBlank { null },
                        type = jikanType,
                        sfw = true,
                        orderBy = if (cleanQuery.isBlank()) "popularity" else "score",
                        sort = if (cleanQuery.isBlank()) "asc" else "desc",
                        page = page,
                        limit = 15
                    ).data.map { it.toWorldComicItem(sourceFilter ?: WorldSource.MANGA) }
                }
            }
        } else null

        val catalogRes = catalogDeferred?.await()
        val archiveRes = archiveDeferred?.await()
        val mangaRes = mangaDeferred?.await()

        val combined = buildList {
            val catalogList = catalogRes?.getOrNull().orEmpty()
            val archiveList = archiveRes?.getOrNull().orEmpty()
            val mangaList = mangaRes?.getOrNull().orEmpty()
            val maxSize = maxOf(catalogList.size, archiveList.size, mangaList.size)
            for (i in 0 until maxSize) {
                if (i < catalogList.size) add(catalogList[i])
                if (i < archiveList.size) add(archiveList[i])
                if (i < mangaList.size) add(mangaList[i])
            }
        }.distinctBy { it.id }

        if (combined.isNotEmpty()) {
            Result.success(combined)
        } else {
            val failure = catalogRes?.exceptionOrNull()
                ?: archiveRes?.exceptionOrNull()
                ?: mangaRes?.exceptionOrNull()
            if (failure != null) Result.failure(failure) else Result.success(emptyList())
        }
    }

    suspend fun fetchRandomUpcomingWorldComic(): Result<WorldComicItem> = runCatching {
        val randomPage = Random.nextInt(1, 5)
        val mangaItems = runCatching {
            jikanApiService.searchManga(
                query = null,
                status = "upcoming",
                sfw = true,
                orderBy = "popularity",
                sort = "asc",
                page = randomPage,
                limit = 20
            ).data.map { it.toWorldComicItem(WorldSource.UPCOMING, isUpcoming = true) }
        }.getOrNull().orEmpty()

        val bookItems = runCatching {
            openLibraryApiService.searchGlobalComics(
                query = "comics first_publish_year:[2026 TO 2028]",
                subject = "comics",
                sort = "new",
                limit = 20,
                page = randomPage
            ).docs.mapNotNull { it.toWorldComicItem(overrideSource = WorldSource.UPCOMING) }
        }.getOrNull().orEmpty()

        val pool = (mangaItems + bookItems)
        require(pool.isNotEmpty()) { "No upcoming comics returned" }
        pool[Random.nextInt(pool.size)]
    }

    suspend fun fetchRandomWorldBookComic(): Result<WorldComicItem> = runCatching {
        val topics = listOf(
            "comics", "graphic novel", "comic strip", "superhero",
            "garfield", "peanuts", "tintin", "asterix", "batman", "spider-man"
        )
        val pickedTopic = topics[Random.nextInt(topics.size)]
        val randomPage = Random.nextInt(1, 12)
        val items = openLibraryApiService.searchGlobalComics(
            query = pickedTopic,
            subject = "comics",
            sort = null,
            limit = 20,
            page = randomPage
        ).docs.mapNotNull { it.toWorldComicItem() }
        val pool = items.filter { it.coverUrl != null }.ifEmpty { items }
        require(pool.isNotEmpty()) { "No world catalog comic returned" }
        pool[Random.nextInt(pool.size)]
    }

    suspend fun fetchRandomWorldManga(): Result<WorldComicItem> = runCatching {
        val randomPage = Random.nextInt(1, 35)
        val items = jikanApiService.searchManga(
            query = null,
            sfw = true,
            orderBy = "popularity",
            sort = "asc",
            page = randomPage,
            limit = 20
        ).data.map { it.toWorldComicItem(WorldSource.MANGA) }
        require(items.isNotEmpty()) { "No world manga returned" }
        items[Random.nextInt(items.size)]
    }

    suspend fun fetchRandomPublicDomainComic(): Result<WorldComicItem> = runCatching {
        val randomPage = Random.nextInt(1, 25)
        val items = archiveApiService.searchPublicDomainComics(
            query = "collection:(comics) AND mediatype:(texts)",
            rows = 20,
            page = randomPage
        ).response?.docs.orEmpty().mapNotNull { it.toWorldComicItem() }
        require(items.isNotEmpty()) { "No public domain comic returned" }
        items[Random.nextInt(items.size)]
    }

    private fun OpenLibraryDocDto.toWorldComicItem(
        overrideSource: WorldSource = WorldSource.WORLD_CATALOG
    ): WorldComicItem? {
        if (key.isBlank() || title.isBlank()) return null
        val authors = authorName.take(2).joinToString(", ").ifBlank { "Global Comic Creator" }
        val yearStr = firstPublishYear?.toString() ?: "Scheduled"
        val ratingStr = ratingsAverage?.let { "★ ${String.format("%.1f", it)}" }
            ?: if (overrideSource == WorldSource.UPCOMING) "Upcoming Release" else "World Catalog"
        val genresList = subject.filter { it.length in 3..26 }.take(3).ifEmpty {
            listOf("Comics", "Graphic Novel")
        }
        return WorldComicItem(
            id = "ol_${key.removePrefix("/works/")}",
            source = overrideSource,
            title = title,
            creator = authors,
            yearOrDate = yearStr,
            genres = genresList,
            synopsis = if (overrideSource == WorldSource.UPCOMING) {
                "Scheduled / new global comic release ($yearStr) indexed in Open Library."
            } else {
                "Indexed in Open Library World Comics & Graphic Novels (${genresList.joinToString(", ")})."
            },
            coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
            statsLabel = "$ratingStr • $yearStr",
            externalReadUrl = "https://openlibrary.org$key"
        )
    }

    private fun JikanMangaDto.toWorldComicItem(
        fallbackSource: WorldSource,
        isUpcoming: Boolean = false
    ): WorldComicItem {
        val resolvedSource = when {
            isUpcoming -> WorldSource.UPCOMING
            type?.lowercase() == "manhwa" -> WorldSource.MANHWA
            type?.lowercase() == "manhua" -> WorldSource.MANHUA
            fallbackSource == WorldSource.MANHWA || fallbackSource == WorldSource.MANHUA -> fallbackSource
            else -> WorldSource.MANGA
        }
        val displayTitle = titleEnglish?.takeIf { it.isNotBlank() } ?: title
        val authorStr = authors.joinToString(", ") { it.name }.ifBlank { "Global Manga Creator" }
        val genreList = genres.map { it.name }.ifEmpty { listOf(type ?: "Manga") }
        val scoreStr = if (isUpcoming) "Upcoming" else score?.let { "Score $it" } ?: "Popular"
        val volStr = volumes?.let { "$it Vols" } ?: chapters?.let { "$it Chs" } ?: (status ?: "Series")
        return WorldComicItem(
            id = "jikan_$malId",
            source = resolvedSource,
            title = displayTitle,
            creator = authorStr,
            yearOrDate = published?.string ?: if (isUpcoming) "TBA / Upcoming" else "Serial",
            genres = genreList,
            synopsis = synopsis?.trim().orEmpty().ifBlank { "Global comic/manga series indexed on MyAnimeList." },
            coverUrl = images?.jpg?.largeImageUrl ?: images?.jpg?.imageUrl,
            statsLabel = "$scoreStr • $volStr",
            externalReadUrl = url.ifBlank { "https://myanimelist.net/manga/$malId" }
        )
    }

    private fun ArchiveComicDocDto.toWorldComicItem(): WorldComicItem? {
        if (identifier.isBlank()) return null
        val cleanTitle = title?.value?.trim()?.takeIf { it.isNotBlank() } ?: identifier
        val cleanCreator = creator?.value?.trim()?.takeIf { it.isNotBlank() } ?: "Golden Age Archive"
        val cleanDesc = description?.value
            ?.replace(Regex("<[^>]*>"), " ")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "Public domain comic issue preserved on the Internet Archive."
        val genresList = subject?.value
            ?.split(";", ",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.take(3)
            ?.ifEmpty { listOf("Public Domain", "Classic Comic") }
            ?: listOf("Public Domain", "Classic Comic")
        val dlText = downloads?.let { "$it downloads" } ?: "Archive Vault"

        return WorldComicItem(
            id = "archive_$identifier",
            source = WorldSource.PUBLIC_DOMAIN,
            title = cleanTitle,
            creator = cleanCreator,
            yearOrDate = date?.value?.take(10) ?: "Classic Era",
            genres = genresList,
            synopsis = cleanDesc,
            coverUrl = "https://archive.org/services/img/$identifier",
            statsLabel = dlText,
            externalReadUrl = "https://archive.org/details/$identifier"
        )
    }
}
