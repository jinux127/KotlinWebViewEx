package com.example.kotlinwebviewex.utils.http

import android.util.Log
import com.example.kotlinwebviewex.model.base.BaseResponse
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription


object Rx : Subscriber<Any> {
   override fun onNext(t: Any?) {
      Log.e("Rx", "onNext")
      if(t is BaseResponse){
         Log.e("Rx","t.data = $t.data t.id = $t.id")
      }
   }

   override fun onError(t: Throwable?) {
      Log.e("Rx", "onError")
      Log.e("Rx", t.toString())
   }

   override fun onComplete() {
      Log.e("Rx", "onComplete")
   }

   override fun onSubscribe(s: Subscription?) {
      Log.e("Rx", "onSubscribe")
      Log.e("Rx", s.toString())
   }
}