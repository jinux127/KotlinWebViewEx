package com.example.kotlinwebviewex.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitApi {
    companion object{
        val BASE_URL = "http://192.168.0.102"

        private fun retrofit():Retrofit{
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .client(client())
                .build()
        }

        private fun gson():Gson{
            return GsonBuilder()
                .setLenient()
                .create()
        }

        private fun client():OkHttpClient{
            val interceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .retryOnConnectionFailure(false)
                .cache(null)
                .build()
        }
    }
    fun getService():RetrofitService{
        return retrofit().create(RetrofitService::class.java)
    }

}