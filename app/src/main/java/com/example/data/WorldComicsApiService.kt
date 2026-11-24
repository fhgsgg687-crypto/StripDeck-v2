package com.example.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// ---- Jikan (MyAnimeList World Manga / Manhwa / Comics) DTOs ----

data class JikanMangaSearchResponse(
    val data: List<JikanMangaDto> = emptyList()
)

data class JikanMangaDto(
    @Json(name = "mal_id") val malId: Int,
    val url: String = "",
    val title: String = "",
    @Json(name = "title_english") val titleEnglish: String? = null,
    val type: String? = null,
    val chapters: Int? = null,
    val volumes: Int? = null,
    val status: String? = null,
    val score: Double? = null,
    val synopsis: String? = null,
    val images: JikanImagesDto? = null,
    val authors: List<JikanNamedDto> = emptyList(),
    val genres: List<JikanNamedDto> = emptyList(),
    val published: JikanPublishedDto? = null
)

data class JikanImagesDto(
    val jpg: JikanImageSetDto? = null
)

data class JikanImageSetDto(
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "large_image_url") val largeImageUrl: String? = null
)

data class JikanNamedDto(
    val name: String = ""
)

data class JikanPublishedDto(
    val string: String? = null
)

data class JikanMangaFullResponse(
    val data: JikanMangaFullDto? = null
)

data class JikanMangaFullDto(
    @Json(name = "mal_id") val malId: Int = 0,
    val title: String = "",
    val chapters: Int? = null,
    val volumes: Int? = null,
    val status: String? = null,
    val published: JikanPublishedDetailDto? = null,
    val serializations: List<JikanNamedDto> = emptyList()
)

data class JikanPublishedDetailDto(
    val from: String? = null,
    val to: String? = null,
    val string: String? = null
)

interface JikanApiService {
    @GET("manga")
    suspend fun searchManga(
        @Query("q") query: String? = null,
        @Query("type") type: String? = null,
        @Query("status") status: String? = null,
        @Query("sfw") sfw: Boolean = true,
        @Query("order_by") orderBy: String = "popularity",
        @Query("sort") sort: String = "asc",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): JikanMangaSearchResponse

    @GET("manga/{id}/full")
    suspend fun getMangaFull(
        @Path("id") id: Int
    ): JikanMangaFullResponse

    companion object {
        private const val BASE_URL = "https://api.jikan.moe/v4/"

        fun create(): JikanApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(JikanApiService::class.java)
        }
    }
}

// ---- Internet Archive (Public Domain Golden Age Comics) DTOs ----

@JvmInline
value class FlexibleText(val value: String)

class FlexibleTextAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): FlexibleText? {
        return when (reader.peek()) {
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                null
            }
            JsonReader.Token.STRING -> FlexibleText(reader.nextString())
            JsonReader.Token.BEGIN_ARRAY -> {
                val items = mutableListOf<String>()
                reader.beginArray()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonReader.Token.STRING) {
                        items.add(reader.nextString())
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endArray()
                FlexibleText(items.joinToString(", "))
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @ToJson
    fun toJson(value: FlexibleText?): String? = value?.value
}

data class ArchiveSearchResponse(
    val response: ArchiveResponseBody? = null
)

data class ArchiveResponseBody(
    val numFound: Int = 0,
    val docs: List<ArchiveComicDocDto> = emptyList()
)

data class ArchiveComicDocDto(
    val identifier: String = "",
    val title: FlexibleText? = null,
    val creator: FlexibleText? = null,
    val date: FlexibleText? = null,
    val description: FlexibleText? = null,
    val downloads: Int? = null,
    val subject: FlexibleText? = null
)

data class ArchiveMetadataResponse(
    val metadata: ArchiveItemMetadataDto? = null
)

data class ArchiveItemMetadataDto(
    val title: FlexibleText? = null,
    val date: FlexibleText? = null,
    val year: FlexibleText? = null,
    val volume: FlexibleText? = null,
    val issue: FlexibleText? = null,
    val publisher: FlexibleText? = null,
    val creator: FlexibleText? = null
)

interface InternetArchiveApiService {
    @GET("advancedsearch.php")
    suspend fun searchPublicDomainComics(
        @Query("q") query: String,
        @Query("fl[]") fields: List<String> = listOf(
            "identifier",
            "title",
            "creator",
            "date",
            "description",
            "downloads",
            "subject"
        ),
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 20,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): ArchiveSearchResponse

    @GET("metadata/{identifier}")
    suspend fun getItemMetadata(
        @Path("identifier") identifier: String
    ): ArchiveMetadataResponse

    companion object {
        private const val BASE_URL = "https://archive.org/"

        fun create(): InternetArchiveApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(FlexibleTextAdapter())
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(InternetArchiveApiService::class.java)
        }
    }
}

// ---- Open Library (Global Comic Books, Graphic Novels & Newspaper Strips) DTOs ----

data class OpenLibrarySearchResponse(
    val numFound: Int = 0,
    val docs: List<OpenLibraryDocDto> = emptyList()
)

data class OpenLibraryDocDto(
    val key: String = "",
    val title: String = "",
    @Json(name = "author_name") val authorName: List<String> = emptyList(),
    @Json(name = "first_publish_year") val firstPublishYear: Int? = null,
    @Json(name = "cover_i") val coverId: Int? = null,
    val subject: List<String> = emptyList(),
    @Json(name = "ratings_average") val ratingsAverage: Double? = null,
    @Json(name = "edition_count") val editionCount: Int? = null
)

data class OpenLibraryEditionsResponse(
    val entries: List<OpenLibraryEditionDto> = emptyList()
)

data class OpenLibraryEditionDto(
    val key: String = "",
    val title: String = "",
    @Json(name = "publish_date") val publishDate: String? = null,
    val publishers: List<String> = emptyList(),
    @Json(name = "number_of_pages") val numberOfPages: Int? = null,
    val series: List<String> = emptyList(),
    @Json(name = "physical_format") val physicalFormat: String? = null
)

interface OpenLibraryApiService {
    @GET("search.json")
    suspend fun searchGlobalComics(
        @Query("q") query: String,
        @Query("subject") subject: String = "comics",
        @Query("sort") sort: String? = "rating",
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,cover_i,subject,ratings_average,edition_count",
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): OpenLibrarySearchResponse

    @GET("works/{workId}/editions.json")
    suspend fun getWorkEditions(
        @Path("workId") workId: String,
        @Query("limit") limit: Int = 25
    ): OpenLibraryEditionsResponse

    companion object {
        private const val BASE_URL = "https://openlibrary.org/"

        fun create(): OpenLibraryApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(12, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenLibraryApiService::class.java)
        }
    }
}
