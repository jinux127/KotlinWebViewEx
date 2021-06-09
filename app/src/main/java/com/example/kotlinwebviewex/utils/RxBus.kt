package com.example.kotlinwebviewex.utils

import android.util.Log
import io.reactivex.subjects.PublishSubject
import java.util.*

object RxBus {

    private val publishSubject = PublishSubject.create<Any>()

//    ex
    val instance = RxBus
    private val subjectTable = Hashtable<String, PublishSubject<Any>>()
//    ex

    fun getSubject(): PublishSubject<Any?> {
        return publishSubject
    }

//    ex
    fun sendEvent(any: Any,key:String="RxBus"){
        Log.e("Rx ex","sendEvent")
        subjectTable
    }
    fun receiveEvent(key : String = "RxBus") : PublishSubject<Any>{
        Log.e("Rx ex","receiveEvent")
        synchronized(this) {
            if (subjectTable.containsKey(key).not()) {
                subjectTable[key] = PublishSubject.create()
            }
            return subjectTable[key]!!
        }
    }
//    ex
}