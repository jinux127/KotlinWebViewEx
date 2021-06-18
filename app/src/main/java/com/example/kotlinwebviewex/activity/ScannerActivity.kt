package com.example.kotlinwebviewex.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.activity.base.BaseActivity
import com.example.kotlinwebviewex.databinding.ActivityScannerBinding
import com.google.zxing.integration.android.IntentIntegrator

class ScannerActivity  : BaseActivity<ActivityScannerBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_scanner

    override fun initView() {
        initQRcodeScanner()
    }

    override fun initDataBinding() {
        TODO("Not yet implemented")
    }

    override fun initAfter() {
        TODO("Not yet implemented")
    }

    private fun initQRcodeScanner() {
        val integrator  = IntentIntegrator(this)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("QR코드를 인증해주세요.")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}