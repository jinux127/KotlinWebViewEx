package com.example.kotlinwebviewex

import android.app.Application
import com.example.kotlinwebviewex.utils.BluetoothHelper
import com.example.kotlinwebviewex.utils.Pref

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Pref.initializeApp(this)
        BluetoothHelper.init(this)
    }
}