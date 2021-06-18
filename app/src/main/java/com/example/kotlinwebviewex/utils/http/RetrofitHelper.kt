package com.example.kotlinwebviewex.utils.http

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private val TAG: String = RetrofitHelper::class.java.simpleName
    var logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.v(TAG, message)
        }
    })

    fun getDefaultOkHttpClient(): OkHttpClient = OkHttpClient().newBuilder().run {
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
        addInterceptor(logging)
        readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
        retryOnConnectionFailure(false)
        build()
    }
}