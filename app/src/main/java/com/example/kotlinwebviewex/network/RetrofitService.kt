package com.example.kotlinwebviewex.network

import com.example.kotlinwebviewex.data.RetrofitData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Path

public interface RetrofitService {

    @POST("/{path}")
    fun postRequest(
        @Path("path")path: String,
        @Body parameters: HashMap<String,Any>
    ): Call<RetrofitData>

}