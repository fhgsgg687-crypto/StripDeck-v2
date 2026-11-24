package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class ComicVineListResponse(
    @Json(name = "status_code") val statusCode: Int = 0,
    val error: String = "",
    @Json(name = "number_of_total_results") val numberOfTotalResults: Int = 0,
    val results: List<ComicVineResourceDto> = emptyList()
)

data class ComicVineSingleResponse(
    @Json(name = "status_code") val statusCode: Int = 0,
    val error: String = "",
    val results: ComicVineResourceDto? = null
)

data class ComicVineResourceDto(
    val id: Int = 0,
    val name: String? = null,
    @Json(name = "issue_number") val issueNumber: String? = null,
    @Json(name = "cover_date") val coverDate: String? = null,
    @Json(name = "store_date") val storeDate: String? = null,
    val description: String? = null,
    val deck: String? = null,
    val image: ComicVineImageDto? = null,
    val volume: ComicVineNamedRefDto? = null,
    val publisher: ComicVineNamedRefDto? = null,
    @Json(name = "resource_type") val resourceType: String? = null,
    @Json(name = "count_of_issues") val countOfIssues: Int? = null,
    @Json(name = "start_year") val startYear: String? = null,
    @Json(name = "site_detail_url") val siteDetailUrl: String? = null,
    @Json(name = "person_credits") val personCredits: List<ComicVinePersonCreditDto> = emptyList()
)

data class ComicVineImageDto(
    @Json(name = "medium_url") val mediumUrl: String? = null,
    @Json(name = "super_url") val superUrl: String? = null,
    @Json(name = "small_url") val smallUrl: String? = null,
    @Json(name = "thumb_url") val thumbUrl: String? = null
)

data class ComicVineNamedRefDto(
    val id: Int? = null,
    val name: String? = null
)

data class ComicVinePersonCreditDto(
    val name: String? = null,
    val role: String? = null
)

interface ComicVineApiService {
    @GET("search/")
    suspend fun searchResources(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("resources") resources: String = "issue,volume",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1,
        @Query("field_list") fieldList: String = DEFAULT_FIELD_LIST
    ): ComicVineListResponse

    @GET("issues/")
    suspend fun getIssues(
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("sort") sort: String = "store_date:desc",
        @Query("filter") filter: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("field_list") fieldList: String = ISSUE_FIELD_LIST
    ): ComicVineListResponse

    @GET("issue/4000-{id}/")
    suspend fun getIssueDetail(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("field_list") fieldList: String = DETAIL_FIELD_LIST
    ): ComicVineSingleResponse

    companion object {
        private const val BASE_URL = "https://comicvine.gamespot.com/api/"

        const val DEFAULT_FIELD_LIST =
            "id,name,issue_number,cover_date,store_date,description,deck,image,volume,publisher,resource_type,count_of_issues,start_year,site_detail_url"

        const val ISSUE_FIELD_LIST =
            "id,name,issue_number,cover_date,store_date,description,deck,image,volume,site_detail_url"

        const val DETAIL_FIELD_LIST =
            "id,name,issue_number,cover_date,store_date,description,deck,image,volume,person_credits,site_detail_url"

        fun create(): ComicVineApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "StripDeck-Android-Collector/1.0")
                        .build()
                    chain.proceed(request)
                }
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ComicVineApiService::class.java)
        }
    }
}
