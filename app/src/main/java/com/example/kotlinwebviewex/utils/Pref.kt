package com.example.kotlinwebviewex.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("StaticFieldLeak")
object Pref {
    private var mContext: Context? =null
    private const val PREFERENCE_NAME = "_pref"
    lateinit var prefs: SharedPreferences
        private set
    private lateinit var editor: SharedPreferences.Editor

    fun initializeApp(context: Context, name:() -> String = { context.packageName.replace('.','_')+ PREFERENCE_NAME}){
        mContext = context
        prefs = context.getSharedPreferences(
            name(),
            Context.MODE_PRIVATE
        )
        editor = prefs.edit()
    }

}