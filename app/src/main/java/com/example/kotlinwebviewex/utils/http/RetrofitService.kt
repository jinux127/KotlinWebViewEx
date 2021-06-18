package com.example.kotlinwebviewex.utils.http

import com.example.kotlinwebviewex.model.base.BaseRequest
import com.example.kotlinwebviewex.model.base.BaseResponse
import io.reactivex.Flowable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetrofitService {
    @Headers("Accept:application/json", "Content-Type:application/json")
    @POST(URL_1)
    fun test0001(@Body testRequest: BaseRequest?): Flowable<BaseResponse>
}