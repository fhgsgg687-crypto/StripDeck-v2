package com.example.data

import com.example.model.XkcdComicDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface XkcdApiService {
    @GET("info.0.json")
    suspend fun getLatestComic(): XkcdComicDto

    @GET("{num}/info.0.json")
    suspend fun getComicByNumber(@Path("num") number: Int): XkcdComicDto

    companion object {
        private const val BASE_URL = "https://xkcd.com/"

        fun create(): XkcdApiService {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(XkcdApiService::class.java)
        }
    }
}
