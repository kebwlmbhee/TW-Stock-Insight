package com.comet.cometstocks.data.network

import com.comet.cometstocks.data.api.StockApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StockApiClient {
    private const val BASE_URL = "https://openapi.twse.com.tw/v1/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: StockApiService by lazy {
        retrofit.create(StockApiService::class.java)
    }
}