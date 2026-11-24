package com.example.model

import com.squareup.moshi.Json

enum class ComicGenre(val label: String, val tagline: String) {
    HUMOR("Daily Humor", "Punchlines & everyday absurdity"),
    ANIMALS("Animals", "Clever pets & woodland mischief"),
    SCI_FI("Sci-Fi & Geek", "Robots, cosmos & code"),
    FAMILY("Family & Life", "Warm Sunday porch stories"),
    OFFICE("Work & Satire", "Coffee, deadlines & desks"),
    ADVENTURE("Adventure", "Backyard quests & capes"),
    RETRO("Retro Classics", "Timeless newsprint nostalgia")
}

enum class MoodTag(val label: String) {
    QUICK_LAUGH("Quick Laugh"),
    CUTE_PETS("Cute Pets"),
    TECH_GEEK("Tech & Geek"),
    NOSTALGIA("80s-90s Vibe"),
    SUNDAY_COLOR("Sunday Color"),
    DEEP_THOUGHT("Shower Thought")
}

enum class CharacterArchetype {
    ORANGE_CAT,
    TABBY_CAT,
    KID_EXPLORER,
    BOXY_ROBOT,
    POTTED_CACTUS,
    TIRED_DEV,
    DOG_DETECTIVE,
    WISE_OWL,
    GRANNY_PUNK,
    ALIEN_GUEST,
    MINIMAL_DUCK,
    SPACE_PILOT,
    STICK_GENIUS
}

data class ComicCharacter(
    val name: String,
    val role: String,
    val bio: String,
    val archetype: CharacterArchetype,
    val accentHex: Long
)

data class StripPanel(
    val caption: String? = null,
    val speaker: String? = null,
    val dialogue: String? = null,
    val thought: String? = null,
    val sfx: String? = null,
    val archetype: CharacterArchetype,
    val secondaryArchetype: CharacterArchetype? = null,
    val pose: String = "happy",
    val scene: String = "room",
    val bgHex: Long = 0xFFFFF8E7
)

data class ComicStrip(
    val id: String,
    val seriesId: String,
    val stripNumber: Int,
    val title: String,
    val publishDate: String,
    val isSundayColor: Boolean,
    val moodTags: List<MoodTag>,
    val panels: List<StripPanel>,
    val authorNote: String
)

data class ComicSeries(
    val id: String,
    val title: String,
    val creator: String,
    val syndicate: String,
    val launchYear: Int,
    val runStatus: String,
    val schedule: String,
    val genre: ComicGenre,
    val rank: Int,
    val subscribersLabel: String,
    val rating: Double,
    val synopsis: String,
    val trivia: String,
    val accentColorHex: Long,
    val coverDrawableName: String? = null,
    val mascotArchetype: CharacterArchetype,
    val characters: List<ComicCharacter>,
    val strips: List<ComicStrip>
)

data class XkcdComicDto(
    val num: Int,
    val title: String,
    val alt: String,
    val img: String,
    val year: String,
    val month: String,
    val day: String,
    @Json(name = "safe_title") val safeTitle: String? = null
)

data class LiveWebcomicStrip(
    val sourceName: String = "XKCD Live",
    val number: Int,
    val title: String,
    val imageUrl: String,
    val altText: String,
    val publishDate: String
)

enum class ExploreSortOption(val label: String) {
    POPULAR("Popular"),
    A_TO_Z("A-Z"),
    NEWEST("Newest"),
    MOST_STRIPS("Most Strips")
}

enum class WorldSource(val label: String) {
    COMICVINE("ComicVine Issues"),
    WORLD_CATALOG("Global Comics & Books"),
    UPCOMING("Upcoming Releases"),
    PUBLIC_DOMAIN("Public Domain Vault"),
    MANGA("Manga (Japan)"),
    MANHWA("Manhwa (Korea)"),
    MANHUA("Manhua (China)")
}

data class WorldComicItem(
    val id: String,
    val source: WorldSource,
    val title: String,
    val creator: String,
    val yearOrDate: String,
    val issueLabel: String? = null,
    val genres: List<String>,
    val synopsis: String,
    val coverUrl: String?,
    val statsLabel: String,
    val externalReadUrl: String
)

data class WorldIssueEdition(
    val issueOrEditionTitle: String,
    val releaseDate: String,
    val publisherOrDetail: String
)

data class IssueTimelineEntry(
    val id: String,
    val seriesId: String,
    val seriesTitle: String,
    val issueNumber: Int,
    val title: String,
    val releaseDate: String,
    val sortKey: Long,
    val isUpcoming: Boolean,
    val statusLabel: String,
    val stripId: String?,
    val accentHex: Long
)

data class UpcomingStripRelease(
    val id: String,
    val seriesId: String,
    val seriesTitle: String,
    val stripNumber: Int,
    val title: String,
    val releaseDate: String,
    val countdownLabel: String,
    val isSundaySpecial: Boolean,
    val teaser: String,
    val accentHex: Long
)

enum class HomeSpotlightTab(val label: String) {
    FOR_YOU("For You"),
    FEATURED("Featured"),
    PICK_OF_THE_WEEK("Pick of the Week")
}

enum class LibraryShelfStatus(val label: String) {
    COLLECTED("Collection"),
    READING("Pull List"),
    WANT_TO_READ("Wishlist"),
    COMPLETED("Logged")
}

data class SavedWorldComic(
    val id: String,
    val sourceName: String,
    val title: String,
    val creator: String,
    val yearOrDate: String,
    val issueLabel: String? = null,
    val genres: List<String>,
    val synopsis: String,
    val coverUrl: String?,
    val statsLabel: String,
    val externalReadUrl: String,
    val shelfStatusName: String = LibraryShelfStatus.COLLECTED.name,
    val addedAtEpochMs: Long = 0L
) {
    fun resolveSource(): WorldSource =
        WorldSource.entries.firstOrNull { it.name == sourceName } ?: WorldSource.WORLD_CATALOG

    fun resolveShelfStatus(): LibraryShelfStatus =
        LibraryShelfStatus.entries.firstOrNull { it.name == shelfStatusName } ?: LibraryShelfStatus.COLLECTED

    fun toWorldComicItem(): WorldComicItem = WorldComicItem(
        id = id,
        source = resolveSource(),
        title = title,
        creator = creator,
        yearOrDate = yearOrDate,
        issueLabel = issueLabel,
        genres = genres,
        synopsis = synopsis,
        coverUrl = coverUrl,
        statsLabel = statsLabel,
        externalReadUrl = externalReadUrl
    )
}

data class SavedWorldComicLibrary(
    val items: List<SavedWorldComic> = emptyList()
)

fun WorldComicItem.toSavedWorldComic(
    shelfStatus: LibraryShelfStatus = LibraryShelfStatus.COLLECTED,
    addedAtEpochMs: Long = System.currentTimeMillis()
): SavedWorldComic = SavedWorldComic(
    id = id,
    sourceName = source.name,
    title = title,
    creator = creator,
    yearOrDate = yearOrDate,
    issueLabel = issueLabel,
    genres = genres,
    synopsis = synopsis,
    coverUrl = coverUrl,
    statsLabel = statsLabel,
    externalReadUrl = externalReadUrl,
    shelfStatusName = shelfStatus.name,
    addedAtEpochMs = addedAtEpochMs
)

