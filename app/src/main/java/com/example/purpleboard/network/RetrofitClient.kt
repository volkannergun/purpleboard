package com.example.purpleboard.network // Adjust package name

import com.example.purpleboard.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Lazy-initialized ApiService instance
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // Configure OkHttpClient with a logging interceptor for debugging
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        // Set logging level for HttpLoggingInterceptor (BODY will log request and response bodies)
        // For production, you might want to use Level.NONE or Level.BASIC
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
            .writeTimeout(30, TimeUnit.SECONDS)   // Write timeout
            .build()
    }

    // Configure Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient) // Use the configured OkHttpClient
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()
    }
}