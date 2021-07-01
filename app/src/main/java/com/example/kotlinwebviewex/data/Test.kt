package com.example.kotlinwebviewex.data

import android.content.SharedPreferences
import com.example.kotlinwebviewex.ext.delegate
import com.example.kotlinwebviewex.utils.Pref

class Test {
    var someString:String by Pref.prefs.delegate("")
}

