package com.example.kotlinwebviewex.utils.http

import android.content.Context
import android.util.Log
import com.example.kotlinwebviewex.model.base.BaseRequest
import com.example.kotlinwebviewex.utils.RxBus
import com.example.kotlinwebviewex.utils.http.RetrofitHelper.getDefaultOkHttpClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object SenderManager {
    private val TAG = SenderManager::class.java.simpleName


    private fun createService(gson: Gson): RetrofitService = Retrofit.Builder().run {
        client(getDefaultOkHttpClient())
        addConverterFactory(GsonConverterFactory.create(gson))
        baseUrl(URL_MAIN)
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        build()
    }.create(RetrofitService::class.java)




    fun send(context: Context?, jsonData: String) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setLenient()
        val gson = gsonBuilder.create()
        val retrofitHttpService: RetrofitService = createService(gson)
        Log.v(TAG, "send body : $jsonData")
        retrofitHttpService.run {
            test0001(BaseRequest(test = jsonData,data = jsonData))
                .subscribeOn(Schedulers.io()) // 어디서 처리할 건지?
                .observeOn(AndroidSchedulers.mainThread()) // 받는 곳 쓰레드
                .subscribe(Rx) // 콜백

        }
    }


}