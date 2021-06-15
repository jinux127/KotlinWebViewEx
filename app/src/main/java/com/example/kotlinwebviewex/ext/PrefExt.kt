package com.example.kotlinwebviewex.ext

import android.content.SharedPreferences
import android.util.Log
import com.example.kotlinwebviewex.utils.Pref
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> pref(
    defaultValue: T,
    type: Class<T>? = null,
    key: (KProperty<*>) -> String = KProperty<*>::name
) = Pref.prefs.delegate(defaultValue, key)

fun <T> SharedPreferences.delegate(
    defaultValue: T,
    key: (KProperty<*>) -> String = KProperty<*>::name
): ReadWriteProperty<Any,T> =
    object : ReadWriteProperty<Any,T>{
        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return (when(defaultValue){
                is Boolean -> getBoolean(key(property),defaultValue)
                is Float -> getFloat(key(property),defaultValue)
                is Int -> getInt(key(property),defaultValue)
                is Long -> getLong(key(property),defaultValue)
                is String -> getString(key(property),defaultValue)
                else -> Log.e("SharedPreferences","ERROR")
            }as T).also {
                Log.e("SharedPreferences","key : ${key(property)}, get : $it")
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            Log.e("SharedPreferences", "key : ${key(property)}, value : $value")
            with(edit()){
                when(value){
                    is Boolean -> putBoolean(key(property), value)
                    is Float -> putFloat(key(property), value)
                    is Int -> putInt(key(property), value)
                    is Long -> putLong(key(property), value)
                    is String -> putString(key(property), value)
                    else -> putString(key(property), value.toString())
                }.apply()
            }
        }
    }
