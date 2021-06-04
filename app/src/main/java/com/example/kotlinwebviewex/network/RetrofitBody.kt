package com.example.kotlinwebviewex.network

import android.util.Log
import com.example.kotlinwebviewex.activity.MainActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class RetrofitBody(mainActivity: MainActivity?) {
    var mMainActivity: MainActivity? = null

    /**
     * RequestBody 정의
     * Created by Jongsuuu on 2020-02-15
     */
    private fun resultRequestBody(mJsonObject: JSONObject): RequestBody? {
        return RequestBody.create(
            MediaType.parse("application/json"),
            mJsonObject.toString()
        )
    }

    fun retroTest01(title:String, content:String): RequestBody? {
        val result = JSONObject()
        try {
            val parms = JSONObject()
            result.put("title", title)
            result.put("content", content)
        } catch (e: JSONException) {
            Log.e("retroTest01", "retroTest01 JSON ERROR - $e")
        } catch (e: Exception) {
            Log.e("retroTest01", "retroTest01 JSON ERROR - $e")
        }
        return resultRequestBody(result)
    }
}