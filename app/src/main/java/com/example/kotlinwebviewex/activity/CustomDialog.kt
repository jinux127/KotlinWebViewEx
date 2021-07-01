package com.example.kotlinwebviewex.activity

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.`interface`.CustomDialogInterface
import com.example.kotlinwebviewex.data.Test
import com.example.kotlinwebviewex.ext.delegate
import com.example.kotlinwebviewex.ext.pref
import com.example.kotlinwebviewex.utils.Pref
import kotlinx.android.synthetic.main.dialog_ex.*

class CustomDialog(context: Context,customDialogInterface: CustomDialogInterface) : Dialog(context), View.OnClickListener {
    val TAG = "CustomDialog"
    private var customDialogInterface: CustomDialogInterface? = null

    init {
        this.customDialogInterface = customDialogInterface

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_ex)
        Log.d(TAG,"onCreate")
        Pref.initializeApp(context)
        dialog_btn_close.setOnClickListener(this)
        dialog_btn_confirm.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        val test = Test()
        when(v){
            dialog_btn_close -> {
                Log.d(TAG,"close")
//                dialog_et.setText(Pref.prefs.getString("TestTemp","default").toString())
                dialog_et.setText(test.someString)
            }
            dialog_btn_confirm -> {
                var TestTemp = dialog_et.text.toString()
                Log.d(TAG,"confirm et_value: $TestTemp")
//                Pref.prefs.edit().putString("TestTemp",TestTemp).apply()
                test.someString = TestTemp
            }
        }
    }
}