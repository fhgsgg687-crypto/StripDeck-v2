package com.example.data

import com.example.BuildConfig
import com.example.model.ComicGenre
import com.example.model.ComicSeries
import com.example.model.ComicStrip
import com.example.model.ExploreSortOption
import com.example.model.IssueTimelineEntry
import com.example.model.LiveWebcomicStrip
import com.example.model.MoodTag
import com.example.model.UpcomingStripRelease
import com.example.model.WorldComicItem
import com.example.model.WorldIssueEdition
import com.example.model.WorldSource
import com.example.model.XkcdComicDto
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.random.Random

class ComicRepository(
    private val xkcdApiService: XkcdApiService = XkcdApiService.create(),
    private val jikanApiService: JikanApiService = JikanApiService.create(),
    private val archiveApiService: InternetArchiveApiService = InternetArchiveApiService.create(),
    private val openLibraryApiService: OpenLibraryApiService = OpenLibraryApiService.create(),
    private val comicVineApiService: ComicVineApiService = ComicVineApiService.create(),
    private val comicVineApiKeyProvider: () -> String = { BuildConfig.COMICVINE_API_KEY }
) {
    companion object {
        const val COMICVINE_PLACEHOLDER = "COMICVINE_API_KEY_PLACEHOLDER"
    }

    private fun resolvedComicVineKey(): String =
        comicVineApiKeyProvider().trim().trim('"', '\'')

    fun isComicVineConfigured(): Boolean {
        val key = resolvedComicVineKey()
        return key.isNotBlank() && key != COMICVINE_PLACEHOLDER
    }
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
        val cleanIssueQ = q.removePrefix("#").trim()
        val filtered = allSeries.filter { series ->
            val matchesGenre = genre == null || series.genre == genre
            val genreKeywords = "${series.genre.label} ${series.genre.name.replace("_", " ")} ${series.genre.tagline}".lowercase()
            val matchesQuery = q.isEmpty() ||
                series.title.lowercase().contains(q) ||
                genreKeywords.contains(q) ||
                series.creator.lowercase().contains(q) ||
                series.synopsis.lowercase().contains(q) ||
                series.characters.any { it.name.lowercase().contains(q) } ||
                series.strips.any { strip ->
                    strip.title.lowercase().contains(q) ||
                        strip.publishDate.lowercase().contains(q) ||
                        (cleanIssueQ.isNotEmpty() && strip.stripNumber.toString() == cleanIssueQ)
                }
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
        val cleanIssueQ = q.removePrefix("#").trim()
        if (q.isEmpty() && genre == null) return emptyList()
        return allStrips.filter { strip ->
            val series = seriesById[strip.seriesId]
            val matchesGenre = genre == null || series?.genre == genre
            val genreKeywords = series?.let {
                "${it.genre.label} ${it.genre.name.replace("_", " ")}".lowercase()
            }.orEmpty()
            val matchesQuery = q.isEmpty() ||
                strip.title.lowercase().contains(q) ||
                strip.publishDate.lowercase().contains(q) ||
                "#${strip.stripNumber}".contains(q) ||
                (cleanIssueQ.isNotEmpty() && strip.stripNumber.toString().contains(cleanIssueQ)) ||
                (series?.title?.lowercase()?.contains(q) == true) ||
                genreKeywords.contains(q)
            matchesGenre && matchesQuery
        }
    }

    fun getChronologicalIssueCalendar(): List<IssueTimelineEntry> {
        val upcomingEntries = getUpcomingStripSchedule().map { up ->
            IssueTimelineEntry(
                id = up.id,
                seriesId = up.seriesId,
                seriesTitle = up.seriesTitle,
                issueNumber = up.stripNumber,
                title = up.title,
                releaseDate = up.releaseDate,
                sortKey = parseIssueSortKey(up.releaseDate),
                isUpcoming = true,
                statusLabel = up.countdownLabel,
                stripId = null,
                accentHex = up.accentHex
            )
        }
        val releasedEntries = allStrips.map { strip ->
            val series = seriesById[strip.seriesId]
            IssueTimelineEntry(
                id = "rel_${strip.id}",
                seriesId = strip.seriesId,
                seriesTitle = series?.title ?: "Comic Strip",
                issueNumber = strip.stripNumber,
                title = strip.title,
                releaseDate = strip.publishDate,
                sortKey = parseIssueSortKey(strip.publishDate),
                isUpcoming = false,
                statusLabel = if (strip.isSundayColor) "Sunday Color" else "Released",
                stripId = strip.id,
                accentHex = series?.accentColorHex ?: 0xFF1F6AF7
            )
        }
        return (upcomingEntries + releasedEntries)
            .sortedWith(compareByDescending<IssueTimelineEntry> { it.sortKey }.thenByDescending { it.issueNumber })
    }

    private fun parseIssueSortKey(dateStr: String): Long {
        val monthMap = mapOf(
            "jan" to 1L, "january" to 1L,
            "feb" to 2L, "february" to 2L,
            "mar" to 3L, "march" to 3L,
            "apr" to 4L, "april" to 4L,
            "may" to 5L,
            "jun" to 6L, "june" to 6L,
            "jul" to 7L, "july" to 7L,
            "aug" to 8L, "august" to 8L,
            "sep" to 9L, "sept" to 9L, "september" to 9L,
            "oct" to 10L, "october" to 10L,
            "nov" to 11L, "november" to 11L,
            "dec" to 12L, "december" to 12L
        )
        val clean = dateStr.trim()
        val mdyRegex = Regex("""([A-Za-z]{3,9})\s+(\d{1,2}),?\s+(\d{4})""")
        mdyRegex.find(clean)?.let { match ->
            val month = monthMap[match.groupValues[1].lowercase()] ?: 1L
            val day = match.groupValues[2].toLongOrNull() ?: 1L
            val year = match.groupValues[3].toLongOrNull() ?: 1900L
            return year * 10000L + month * 100L + day
        }
        val isoRegex = Regex("""(\d{4})-(\d{1,2})-(\d{1,2})""")
        isoRegex.find(clean)?.let { match ->
            val year = match.groupValues[1].toLongOrNull() ?: 1900L
            val month = match.groupValues[2].toLongOrNull() ?: 1L
            val day = match.groupValues[3].toLongOrNull() ?: 1L
            return year * 10000L + month * 100L + day
        }
        val yearRegex = Regex("""(\d{4})""")
        yearRegex.find(clean)?.let { match ->
            val year = match.groupValues[1].toLongOrNull() ?: 1900L
            return year * 10000L
        }
        return 0L
    }

    fun extractIssueOrVolumeLabel(title: String, explicitIssue: String? = null): String? {
        val exp = explicitIssue?.trim()?.takeIf { it.isNotBlank() }
        if (exp != null) {
            return if (exp.all { it.isDigit() }) "Issue #$exp" else exp
        }
        val issueRegex = Regex("""(?i)\b(?:issue|iss\.?|no\.?)\s*#?\s*(\d+[a-z]?)\b""")
        issueRegex.find(title)?.let { return "Issue #${it.groupValues[1]}" }

        val hashRegex = Regex("""(?i)(?:^|\s|\()#(\d+[a-z]?)\b""")
        hashRegex.find(title)?.let { return "Issue #${it.groupValues[1]}" }

        val volRegex = Regex("""(?i)\b(?:vol\.?|volume|book|pt\.?|part)\s*#?\s*(\d+)\b""")
        volRegex.find(title)?.let { return "Vol. ${it.groupValues[1]}" }

        return null
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
        val cvKey = resolvedComicVineKey().takeIf { isComicVineConfigured() }

        if (sourceFilter == WorldSource.COMICVINE) {
            if (cvKey == null) {
                return@coroutineScope Result.success(emptyList())
            }
            return@coroutineScope runCatching {
                val resp = if (cleanQuery.isBlank()) {
                    comicVineApiService.getIssues(
                        apiKey = cvKey,
                        sort = "store_date:desc",
                        limit = 20,
                        offset = (page - 1) * 20
                    )
                } else {
                    comicVineApiService.searchResources(
                        apiKey = cvKey,
                        query = cleanQuery,
                        resources = "issue,volume",
                        limit = 20,
                        page = page
                    )
                }
                if (resp.statusCode != 1) {
                    throw IllegalStateException(resp.error.ifBlank { "ComicVine error ${resp.statusCode}" })
                }
                val mapped = resp.results.mapNotNull { it.toWorldComicItem(WorldSource.COMICVINE) }
                if (cleanQuery.isNotBlank()) {
                    mapped.sortedByDescending { it.id.startsWith("cv_iss_") }
                } else {
                    mapped
                }
            }
        }

        if (sourceFilter == WorldSource.UPCOMING) {
            val upcomingComicVineDeferred = if (cvKey != null) {
                async {
                    runCatching {
                        val resp = if (cleanQuery.isBlank()) {
                            comicVineApiService.getIssues(
                                apiKey = cvKey,
                                sort = "store_date:desc",
                                limit = 15,
                                offset = (page - 1) * 15
                            )
                        } else {
                            comicVineApiService.searchResources(
                                apiKey = cvKey,
                                query = cleanQuery,
                                resources = "issue",
                                limit = 15,
                                page = page
                            )
                        }
                        if (resp.statusCode != 1) {
                            throw IllegalStateException(resp.error)
                        }
                        resp.results.mapNotNull { it.toWorldComicItem(WorldSource.UPCOMING) }
                    }
                }
            } else null

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

            val upCv = upcomingComicVineDeferred?.await()
            val upManga = upcomingMangaDeferred.await()
            val upBooks = upcomingBooksDeferred.await()
            val combinedUpcoming = buildList {
                val cvList = upCv?.getOrNull().orEmpty()
                val mList = upManga.getOrNull().orEmpty()
                val bList = upBooks.getOrNull().orEmpty()
                val maxSize = maxOf(cvList.size, mList.size, bList.size)
                for (i in 0 until maxSize) {
                    if (i < cvList.size) add(cvList[i])
                    if (i < bList.size) add(bList[i])
                    if (i < mList.size) add(mList[i])
                }
            }.distinctBy { it.id }

            return@coroutineScope if (combinedUpcoming.isNotEmpty()) {
                Result.success(combinedUpcoming)
            } else {
                val err = upCv?.exceptionOrNull() ?: upManga.exceptionOrNull() ?: upBooks.exceptionOrNull()
                if (err != null) Result.failure(err) else Result.success(emptyList())
            }
        }

        val fetchComicVine = cvKey != null && sourceFilter == null
        val fetchCatalog = sourceFilter == null || sourceFilter == WorldSource.WORLD_CATALOG
        val fetchArchive = sourceFilter == null || sourceFilter == WorldSource.PUBLIC_DOMAIN
        val jikanType = when (sourceFilter) {
            WorldSource.MANGA -> "manga"
            WorldSource.MANHWA -> "manhwa"
            WorldSource.MANHUA -> "manhua"
            else -> null
        }
        val fetchJikan = sourceFilter == null || jikanType != null

        val comicVineDeferred = if (fetchComicVine && cvKey != null) {
            async {
                runCatching {
                    val resp = if (cleanQuery.isBlank()) {
                        comicVineApiService.getIssues(
                            apiKey = cvKey,
                            sort = "store_date:desc",
                            limit = 12,
                            offset = (page - 1) * 12
                        )
                    } else {
                        comicVineApiService.searchResources(
                            apiKey = cvKey,
                            query = cleanQuery,
                            resources = "issue,volume",
                            limit = 12,
                            page = page
                        )
                    }
                    if (resp.statusCode != 1) {
                        throw IllegalStateException(resp.error)
                    }
                    resp.results.mapNotNull { it.toWorldComicItem(WorldSource.COMICVINE) }
                }
            }
        } else null

        val catalogDeferred = if (fetchCatalog) {
            async {
                runCatching {
                    val olQuery = cleanQuery.ifBlank { "\"graphic novel\" OR \"comic book\"" }
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

        val cvRes = comicVineDeferred?.await()
        val catalogRes = catalogDeferred?.await()
        val archiveRes = archiveDeferred?.await()
        val mangaRes = mangaDeferred?.await()

        val combined = buildList {
            val cvList = cvRes?.getOrNull().orEmpty()
            val catalogList = catalogRes?.getOrNull().orEmpty()
            val archiveList = archiveRes?.getOrNull().orEmpty()
            val mangaList = mangaRes?.getOrNull().orEmpty()
            val maxSize = maxOf(cvList.size, catalogList.size, archiveList.size, mangaList.size)
            for (i in 0 until maxSize) {
                if (i < cvList.size) add(cvList[i])
                if (i < catalogList.size) add(catalogList[i])
                if (i < archiveList.size) add(archiveList[i])
                if (i < mangaList.size) add(mangaList[i])
            }
        }.distinctBy { it.id }

        if (combined.isNotEmpty()) {
            Result.success(combined)
        } else {
            val failure = cvRes?.exceptionOrNull()
                ?: catalogRes?.exceptionOrNull()
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
        require(items.isNotEmpty()) { "No classic archive comic returned" }
        items[Random.nextInt(items.size)]
    }

    suspend fun fetchRandomComicVineIssue(): Result<WorldComicItem> = runCatching {
        val cvKey = resolvedComicVineKey()
        require(isComicVineConfigured()) { "COMICVINE_API_KEY is not configured" }
        val randomOffset = Random.nextInt(0, 600)
        val resp = comicVineApiService.getIssues(
            apiKey = cvKey,
            sort = "store_date:desc",
            limit = 20,
            offset = randomOffset
        )
        if (resp.statusCode != 1) {
            throw IllegalStateException(resp.error.ifBlank { "ComicVine error ${resp.statusCode}" })
        }
        val items = resp.results.mapNotNull { it.toWorldComicItem(WorldSource.COMICVINE) }
        val pool = items.filter { it.coverUrl != null }.ifEmpty { items }
        require(pool.isNotEmpty()) { "No ComicVine issue returned" }
        pool[Random.nextInt(pool.size)]
    }

    suspend fun fetchLiveComicVineReleaseCalendar(page: Int = 1): Result<List<WorldComicItem>> = runCatching {
        val cvKey = resolvedComicVineKey()
        require(isComicVineConfigured()) { "COMICVINE_API_KEY is not configured" }
        val resp = comicVineApiService.getIssues(
            apiKey = cvKey,
            sort = "store_date:desc",
            limit = 30,
            offset = (page - 1) * 30
        )
        if (resp.statusCode != 1) {
            throw IllegalStateException(resp.error.ifBlank { "ComicVine error ${resp.statusCode}" })
        }
        resp.results.mapNotNull { it.toWorldComicItem(WorldSource.COMICVINE) }
    }

    suspend fun fetchRandomWorldComicAny(): Result<WorldComicItem> {
        val rollChoices = if (isComicVineConfigured()) 5 else 4
        return when (Random.nextInt(rollChoices)) {
            0 -> fetchRandomWorldBookComic()
            1 -> fetchRandomUpcomingWorldComic()
            2 -> fetchRandomWorldManga()
            3 -> fetchRandomPublicDomainComic()
            else -> fetchRandomComicVineIssue()
        }
    }

    private suspend fun fetchComicVineIssuesByTitle(title: String, cvKey: String): List<WorldIssueEdition> {
        val cleanTitle = title
            .replace(Regex("\\s*\\([^)]*\\)"), "")
            .replace(Regex("\\s*#\\d+.*$"), "")
            .trim()
        if (cleanTitle.length < 2) return emptyList()
        val searchResp = runCatching {
            comicVineApiService.searchResources(
                apiKey = cvKey,
                query = cleanTitle,
                resources = "volume,issue",
                limit = 15,
                page = 1
            )
        }.getOrNull() ?: return emptyList()
        if (searchResp.statusCode != 1) return emptyList()

        val matchedVolume = searchResp.results.firstOrNull {
            it.resourceType.equals("volume", ignoreCase = true) || it.countOfIssues != null
        }
        if (matchedVolume != null && matchedVolume.id > 0) {
            val volIssuesResp = runCatching {
                comicVineApiService.getIssues(
                    apiKey = cvKey,
                    filter = "volume:${matchedVolume.id}",
                    sort = "store_date:desc",
                    limit = 40
                )
            }.getOrNull()
            if (volIssuesResp != null && volIssuesResp.statusCode == 1 && volIssuesResp.results.isNotEmpty()) {
                return volIssuesResp.results
                    .sortedByDescending { it.issueNumber?.trim()?.toDoubleOrNull() ?: -1.0 }
                    .map { iss ->
                        val issNum = iss.issueNumber?.takeIf { it.isNotBlank() } ?: "?"
                        val issTitle = iss.name?.takeIf { it.isNotBlank() }
                        val label = if (issTitle != null) "Issue #$issNum • $issTitle" else "Issue #$issNum"
                        val relDate = iss.storeDate?.takeIf { it.isNotBlank() }
                            ?: iss.coverDate?.takeIf { it.isNotBlank() }
                            ?: "Indexed"
                        WorldIssueEdition(
                            issueOrEditionTitle = label,
                            releaseDate = relDate,
                            publisherOrDetail = iss.volume?.name ?: matchedVolume.name ?: cleanTitle
                        )
                    }
            }
        }

        return searchResp.results
            .filter { it.resourceType.equals("issue", ignoreCase = true) || !it.issueNumber.isNullOrBlank() }
            .sortedByDescending { it.issueNumber?.trim()?.toDoubleOrNull() ?: -1.0 }
            .map { iss ->
                val issNum = iss.issueNumber?.takeIf { it.isNotBlank() } ?: "?"
                val vol = iss.volume?.name?.takeIf { it.isNotBlank() }
                val issTitle = iss.name?.takeIf { it.isNotBlank() }
                val titlePart = listOfNotNull(vol, issTitle).joinToString(": ")
                val label = if (titlePart.isNotBlank()) "Issue #$issNum • $titlePart" else "Issue #$issNum"
                val relDate = iss.storeDate?.takeIf { it.isNotBlank() }
                    ?: iss.coverDate?.takeIf { it.isNotBlank() }
                    ?: iss.startYear?.takeIf { it.isNotBlank() }
                    ?: "Indexed"
                WorldIssueEdition(
                    issueOrEditionTitle = label,
                    releaseDate = relDate,
                    publisherOrDetail = iss.publisher?.name ?: vol ?: "ComicVine"
                )
            }
    }

    suspend fun fetchWorldComicIssueHistory(item: WorldComicItem): Result<List<WorldIssueEdition>> = runCatching {
        val cvKey = resolvedComicVineKey().takeIf { isComicVineConfigured() }
        when {
            item.id.startsWith("ol_") -> coroutineScope {
                val workId = item.id.removePrefix("ol_")
                val editionsDeferred = async {
                    runCatching { openLibraryApiService.getWorkEditions(workId, limit = 25).entries }.getOrNull().orEmpty()
                }
                val cvCrossDeferred = if (cvKey != null) {
                    async { fetchComicVineIssuesByTitle(item.title, cvKey) }
                } else null

                val editions = editionsDeferred.await()
                val cvIssues = cvCrossDeferred?.await().orEmpty()
                val olMapped = editions.mapNotNull { ed ->
                    val pubDate = ed.publishDate?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                    val issueOrTitle = buildString {
                        val extracted = extractIssueOrVolumeLabel(ed.title)
                        if (extracted != null) {
                            append(extracted)
                            append(" • ")
                        }
                        append(ed.title.ifBlank { item.title })
                        ed.physicalFormat?.takeIf { it.isNotBlank() }?.let { fmt ->
                            append(" ($fmt)")
                        }
                    }
                    val detailParts = buildList {
                        ed.publishers.firstOrNull()?.takeIf { it.isNotBlank() }?.let { add(it) }
                        ed.numberOfPages?.takeIf { it > 0 }?.let { add("$it pages") }
                        ed.series.firstOrNull()?.takeIf { it.isNotBlank() }?.let { add(it) }
                    }
                    WorldIssueEdition(
                        issueOrEditionTitle = issueOrTitle,
                        releaseDate = pubDate,
                        publisherOrDetail = detailParts.joinToString(" • ").ifBlank { "Open Library Edition" }
                    )
                }.distinctBy { "${it.issueOrEditionTitle}_${it.releaseDate}" }

                val merged = (cvIssues + olMapped).distinctBy { "${it.issueOrEditionTitle}_${it.releaseDate}" }
                merged.ifEmpty {
                    listOf(
                        WorldIssueEdition(
                            issueOrEditionTitle = item.issueLabel ?: "First Indexed Edition",
                            releaseDate = item.yearOrDate,
                            publisherOrDetail = "${item.creator} • Open Library Catalog"
                        )
                    )
                }
            }
            item.id.startsWith("archive_") -> coroutineScope {
                val identifier = item.id.removePrefix("archive_")
                val metaDeferred = async {
                    runCatching { archiveApiService.getItemMetadata(identifier).metadata }.getOrNull()
                }
                val cvCrossDeferred = if (cvKey != null) {
                    async { fetchComicVineIssuesByTitle(item.title, cvKey) }
                } else null

                val meta = metaDeferred.await()
                val cvIssues = cvCrossDeferred?.await().orEmpty()
                val exactDate = meta?.date?.value?.trim()?.takeIf { it.isNotBlank() }
                    ?: meta?.year?.value?.trim()?.takeIf { it.isNotBlank() }
                    ?: item.yearOrDate
                val explicitIssue = meta?.issue?.value?.trim()?.takeIf { it.isNotBlank() }
                    ?: meta?.volume?.value?.trim()?.takeIf { it.isNotBlank() }?.let { "Vol. $it" }
                val issueLabel = extractIssueOrVolumeLabel(meta?.title?.value ?: item.title, explicitIssue)
                    ?: "Archival Issue"
                val detail = listOfNotNull(
                    meta?.publisher?.value?.trim()?.takeIf { it.isNotBlank() },
                    meta?.creator?.value?.trim()?.takeIf { it.isNotBlank() },
                    "Internet Archive Public Domain"
                ).joinToString(" • ")
                val primaryEntry = WorldIssueEdition(
                    issueOrEditionTitle = "$issueLabel — ${meta?.title?.value ?: item.title}",
                    releaseDate = exactDate,
                    publisherOrDetail = detail
                )
                (listOf(primaryEntry) + cvIssues).distinctBy { "${it.issueOrEditionTitle}_${it.releaseDate}" }
            }
            item.id.startsWith("jikan_") -> {
                val malId = item.id.removePrefix("jikan_").toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid Jikan ID")
                val full = jikanApiService.getMangaFull(malId).data
                val fromDate = full?.published?.from?.take(10)
                val toDate = full?.published?.to?.take(10)
                val mag = full?.serializations?.joinToString(", ") { it.name }?.takeIf { it.isNotBlank() }
                    ?: "Serial Publication"
                buildList {
                    if (!fromDate.isNullOrBlank()) {
                        add(
                            WorldIssueEdition(
                                issueOrEditionTitle = "Chapter #1 / Debut Release",
                                releaseDate = fromDate,
                                publisherOrDetail = "Serialized in $mag"
                            )
                        )
                    }
                    val totalSummary = buildList {
                        full?.volumes?.let { add("$it Volumes") }
                        full?.chapters?.let { add("$it Chapters") }
                    }.joinToString(" • ").ifBlank { item.issueLabel ?: (full?.status ?: "Ongoing / Scheduled") }
                    add(
                        WorldIssueEdition(
                            issueOrEditionTitle = totalSummary,
                            releaseDate = full?.published?.string ?: item.yearOrDate,
                            publisherOrDetail = if (!toDate.isNullOrBlank()) "Ended $toDate • ${full.status ?: ""}" else (full?.status ?: "MyAnimeList Index")
                        )
                    )
                }
            }
            item.id.startsWith("cv_vol_") -> {
                if (cvKey == null) return@runCatching emptyList()
                val volId = item.id.removePrefix("cv_vol_").toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid ComicVine Volume ID")
                val resp = comicVineApiService.getIssues(
                    apiKey = cvKey,
                    filter = "volume:$volId",
                    sort = "store_date:desc",
                    limit = 50
                )
                if (resp.statusCode != 1) {
                    throw IllegalStateException(resp.error.ifBlank { "ComicVine error ${resp.statusCode}" })
                }
                resp.results
                    .sortedByDescending { it.issueNumber?.trim()?.toDoubleOrNull() ?: -1.0 }
                    .map { iss ->
                        val issNum = iss.issueNumber?.takeIf { it.isNotBlank() } ?: "?"
                        val issTitle = iss.name?.takeIf { it.isNotBlank() }
                        val label = if (issTitle != null) "Issue #$issNum • $issTitle" else "Issue #$issNum"
                        val relDate = iss.storeDate?.takeIf { it.isNotBlank() }
                            ?: iss.coverDate?.takeIf { it.isNotBlank() }
                            ?: item.yearOrDate
                        WorldIssueEdition(
                            issueOrEditionTitle = label,
                            releaseDate = relDate,
                            publisherOrDetail = iss.volume?.name ?: item.title
                        )
                    }
            }
            item.id.startsWith("cv_iss_") -> coroutineScope {
                if (cvKey == null) return@coroutineScope emptyList()
                val issueId = item.id.removePrefix("cv_iss_").toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid ComicVine Issue ID")
                val resp = comicVineApiService.getIssueDetail(id = issueId, apiKey = cvKey)
                if (resp.statusCode != 1) {
                    throw IllegalStateException(resp.error.ifBlank { "ComicVine error ${resp.statusCode}" })
                }
                val detail = resp.results
                val volumeId = detail?.volume?.id
                val siblingIssues = if (volumeId != null && volumeId > 0) {
                    runCatching {
                        val volResp = comicVineApiService.getIssues(
                            apiKey = cvKey,
                            filter = "volume:$volumeId",
                            sort = "store_date:desc",
                            limit = 40
                        )
                        if (volResp.statusCode == 1) {
                            volResp.results
                                .sortedByDescending { it.issueNumber?.trim()?.toDoubleOrNull() ?: -1.0 }
                                .map { iss ->
                                    val issNum = iss.issueNumber?.takeIf { it.isNotBlank() } ?: "?"
                                    val issTitle = iss.name?.takeIf { it.isNotBlank() }
                                    val label = if (issTitle != null) "Issue #$issNum • $issTitle" else "Issue #$issNum"
                                    val relDate = iss.storeDate?.takeIf { it.isNotBlank() }
                                        ?: iss.coverDate?.takeIf { it.isNotBlank() }
                                        ?: item.yearOrDate
                                    WorldIssueEdition(
                                        issueOrEditionTitle = label,
                                        releaseDate = relDate,
                                        publisherOrDetail = iss.volume?.name ?: item.creator
                                    )
                                }
                        } else emptyList()
                    }.getOrNull().orEmpty()
                } else emptyList()

                val detailCards = buildList {
                    detail?.storeDate?.takeIf { it.isNotBlank() }?.let { storeDate ->
                        add(
                            WorldIssueEdition(
                                issueOrEditionTitle = "${item.issueLabel ?: "Single Issue"} • Store Release",
                                releaseDate = storeDate,
                                publisherOrDetail = detail.volume?.name ?: item.creator
                            )
                        )
                    }
                    detail?.coverDate?.takeIf { it.isNotBlank() }?.let { coverDate ->
                        add(
                            WorldIssueEdition(
                                issueOrEditionTitle = "Cover Date",
                                releaseDate = coverDate,
                                publisherOrDetail = "ComicVine Official Issue #${detail.issueNumber ?: "?"}"
                            )
                        )
                    }
                    val credits = detail?.personCredits
                        .orEmpty()
                        .mapNotNull { pc ->
                            val n = pc.name?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                            val r = pc.role?.trim()?.takeIf { it.isNotBlank() }
                            if (r != null) "$n ($r)" else n
                        }
                        .take(5)
                    if (credits.isNotEmpty()) {
                        add(
                            WorldIssueEdition(
                                issueOrEditionTitle = "Creators & Credits",
                                releaseDate = detail?.storeDate ?: detail?.coverDate ?: item.yearOrDate,
                                publisherOrDetail = credits.joinToString(" • ")
                            )
                        )
                    }
                }

                val combined = (detailCards + siblingIssues).distinctBy { "${it.issueOrEditionTitle}_${it.releaseDate}" }
                combined.ifEmpty {
                    listOf(
                        WorldIssueEdition(
                            issueOrEditionTitle = item.issueLabel ?: "Single Issue",
                            releaseDate = item.yearOrDate,
                            publisherOrDetail = item.creator
                        )
                    )
                }
            }
            else -> emptyList()
        }
    }

    private fun OpenLibraryDocDto.toWorldComicItem(
        overrideSource: WorldSource = WorldSource.WORLD_CATALOG
    ): WorldComicItem? {
        if (key.isBlank() || title.isBlank()) return null
        val authors = authorName.take(2).joinToString(", ").ifBlank { "Global Comic Creator" }
        val yearStr = firstPublishYear?.toString() ?: "Scheduled"
        val issueBadge = extractIssueOrVolumeLabel(title)
            ?: editionCount?.takeIf { it > 1 }?.let { "$it Editions" }
            ?: "Graphic Novel"
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
            issueLabel = issueBadge,
            genres = genresList,
            synopsis = if (overrideSource == WorldSource.UPCOMING) {
                "Scheduled / new global comic release ($yearStr) indexed in Open Library."
            } else {
                "Indexed in Open Library World Comics & Graphic Novels (${genresList.joinToString(", ")})."
            },
            coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" },
            statsLabel = "$ratingStr • Released: $yearStr",
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
        val issueBadge = when {
            chapters != null && volumes != null -> "$volumes Vols • $chapters Chs"
            chapters != null -> "$chapters Chapters"
            volumes != null -> "$volumes Volumes"
            else -> extractIssueOrVolumeLabel(displayTitle)
        }
        val releaseWindow = published?.string?.takeIf { it.isNotBlank() }
            ?: if (isUpcoming) "TBA / Upcoming" else "Serial"
        return WorldComicItem(
            id = "jikan_$malId",
            source = resolvedSource,
            title = displayTitle,
            creator = authorStr,
            yearOrDate = releaseWindow,
            issueLabel = issueBadge,
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
            ?: "Cataloged archival comic edition indexed in the Classic Vault."
        val genresList = subject?.value
            ?.split(";", ",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.take(3)
            ?.ifEmpty { listOf("Classic Era", "Archive Catalog") }
            ?: listOf("Classic Era", "Archive Catalog")
        val issueBadge = extractIssueOrVolumeLabel(cleanTitle)
        val releaseDateText = date?.value?.take(10)?.takeIf { it.isNotBlank() } ?: "Classic Era"

        return WorldComicItem(
            id = "archive_$identifier",
            source = WorldSource.PUBLIC_DOMAIN,
            title = cleanTitle,
            creator = cleanCreator,
            yearOrDate = releaseDateText,
            issueLabel = issueBadge,
            genres = genresList,
            synopsis = cleanDesc,
            coverUrl = "https://archive.org/services/img/$identifier",
            statsLabel = "Classic Vault • Released: $releaseDateText",
            externalReadUrl = "https://archive.org/details/$identifier"
        )
    }

    internal fun ComicVineResourceDto.toWorldComicItem(
        overrideSource: WorldSource = WorldSource.COMICVINE
    ): WorldComicItem? {
        if (id <= 0) return null
        val isIssue = resourceType.equals("issue", ignoreCase = true) ||
            !issueNumber.isNullOrBlank() ||
            volume != null
        val cleanName = name?.trim()?.takeIf { it.isNotBlank() }
        val volName = volume?.name?.trim()?.takeIf { it.isNotBlank() }
        val issNum = issueNumber?.trim()?.takeIf { it.isNotBlank() }

        val displayTitle = if (isIssue) {
            val base = when {
                volName != null && issNum != null -> "$volName #$issNum"
                volName != null -> volName
                cleanName != null && issNum != null -> "$cleanName #$issNum"
                else -> cleanName ?: "Comic Issue #$id"
            }
            if (cleanName != null && volName != null && !cleanName.equals(volName, ignoreCase = true)) {
                "$base: $cleanName"
            } else {
                base
            }
        } else {
            cleanName ?: "Comic Series #$id"
        }

        val releaseDate = storeDate?.trim()?.takeIf { it.isNotBlank() }
            ?: coverDate?.trim()?.takeIf { it.isNotBlank() }
            ?: startYear?.trim()?.takeIf { it.isNotBlank() }
            ?: "Indexed"

        val issueBadge = if (isIssue) {
            issNum?.let { "Issue #$it" } ?: "Single Issue"
        } else {
            countOfIssues?.takeIf { it > 0 }?.let { "$it Issues" } ?: "Series"
        }

        val creatorOrPublisher = publisher?.name?.trim()?.takeIf { it.isNotBlank() }
            ?: volName
            ?: "ComicVine Global Index"

        val rawDesc = description?.takeIf { it.isNotBlank() } ?: deck
        val cleanSynopsis = rawDesc
            ?.replace(Regex("<[^>]*>"), " ")
            ?.replace("&nbsp;", " ")
            ?.replace("&amp;", "&")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "Single-issue & volume metadata indexed by ComicVine ($creatorOrPublisher • $releaseDate)."

        val genresList = buildList {
            add(if (isIssue) "Single Issue" else "Comic Series")
            publisher?.name?.trim()?.takeIf { it.isNotBlank() }?.let { add(it) }
            if (isIssue && volName != null) add(volName)
        }.distinct().take(3)

        val cover = image?.superUrl?.takeIf { it.isNotBlank() }
            ?: image?.mediumUrl?.takeIf { it.isNotBlank() }
            ?: image?.smallUrl?.takeIf { it.isNotBlank() }
            ?: image?.thumbUrl?.takeIf { it.isNotBlank() }

        return WorldComicItem(
            id = if (isIssue) "cv_iss_$id" else "cv_vol_$id",
            source = overrideSource,
            title = displayTitle,
            creator = creatorOrPublisher,
            yearOrDate = releaseDate,
            issueLabel = issueBadge,
            genres = genresList,
            synopsis = cleanSynopsis,
            coverUrl = cover,
            statsLabel = "ComicVine • $creatorOrPublisher • $issueBadge",
            externalReadUrl = siteDetailUrl?.takeIf { it.isNotBlank() } ?: "https://comicvine.gamespot.com/"
        )
    }
}
