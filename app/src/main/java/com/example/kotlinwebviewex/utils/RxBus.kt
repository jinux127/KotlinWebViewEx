package com.example.kotlinwebviewex.utils

import android.util.Log
import io.reactivex.subjects.PublishSubject
import java.util.*

object RxBus {

    private val publishSubject = PublishSubject.create<Any>()

    fun getSubject(): PublishSubject<Any?> {
        return publishSubject
    }

}