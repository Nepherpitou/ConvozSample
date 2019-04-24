package co.tula.convozsample.data

import co.tula.convozsample.BuildConfig
import co.tula.convozsample.extensions.IO
import co.tula.convozsample.extensions.io
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.RuntimeException

interface Repository {
    suspend fun search(query: String, offset: Int = 0, limit: Int = 20): IO<List<GifObject>, Exception>
}

interface ApiClient {

    @GET("/v1/gifs/search")
    fun search(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<GiphyResponse<List<GifObject>>>
}

class RepositoryImpl : Repository {

    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
        )
        .baseUrl(BuildConfig.BASE_URL)
        .build()
    val client: ApiClient = retrofit.create(ApiClient::class.java)

    override suspend fun search(query: String, offset: Int, limit: Int) = io {
        val response = client.search(BuildConfig.API_KEY, query, offset, limit).execute()
        response.body()?.data ?: throw RuntimeException("Failed to load images with code ${response.code()}")
    }

}