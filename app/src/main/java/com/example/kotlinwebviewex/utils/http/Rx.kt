package com.example.kotlinwebviewex.utils.http

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.kotlinwebviewex.ext.checkPermission
import com.example.kotlinwebviewex.ext.now
import com.example.kotlinwebviewex.model.RxBusData
import com.example.kotlinwebviewex.model.base.BaseResponse
import com.example.kotlinwebviewex.utils.RXBUS_TYPE_INTRO_TO_MAIN
import com.example.kotlinwebviewex.utils.RXBUS_TYPE_PERMISSION
import com.example.kotlinwebviewex.utils.RxBus
import com.example.kotlinwebviewex.utils.rxBus_type
import kotlinx.android.synthetic.main.activity_main.*
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


object Rx : Subscriber<Any> {
   override fun onNext(t: Any?) {
      Log.e("Rx", "onNext t: ${t.toString()}")
//      Log.e("Rx", "$t")
//      if(t is BaseResponse){
//         Log.e("tag","BaseResponse 레트로핏 출력")
//         Log.e("tag","id: ${t.id}")
//         Log.e("tag","id: ${t.data}")
//      }
//      var rxBusData = RxBusData("MainActivity",type = "Rx",data = t)
//      RxBus.getSubject().onNext(rxBusData)
   }


   override fun onError(t: Throwable?) {
      Log.e("Rx",  "onError")
      Log.e("Rx", t.toString())
   }

   override fun onComplete() {
      Log.e("Rx", "onComplete")
   }

   override fun onSubscribe(s: Subscription?) {
      Log.e("Rx", "onSubscribe")
      Log.e("Rx", "s.toString() : ${s.toString()}")
   }
}