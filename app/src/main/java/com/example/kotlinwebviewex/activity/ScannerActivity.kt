package com.example.kotlinwebviewex.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.activity.base.BaseActivity
import com.example.kotlinwebviewex.databinding.ActivityScannerBinding
import com.example.kotlinwebviewex.ext.toastShow
import com.example.kotlinwebviewex.utils.BluetoothHelper
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager

class ScannerActivity  : BaseActivity<ActivityScannerBinding>() {
    private val TAG = "ScannerActivity"
    override val layoutId: Int
        get() = R.layout.activity_scanner
    lateinit var captureManager:CaptureManager

    companion object {
        const val RESULT_DATA = "result_data"
    }

    override fun initView() {
        viewDataBinding.bandToolbar?.let {
            setSupportActionBar(it)
            title = "등록"
        }
    }

    override fun initDataBinding() {
    }

    override fun initAfter() {
        with(viewDataBinding){
            tvBandEnroll.setOnClickListener {
                when{
                    "" == etBandName.text.toString() -> toastShow("입력된 이름이 없습니다. 다시 확인해 주세요.")
                    etBandName.text.length >=6 ->{
                        val name = etBandName.text.toString().run {
                            "KsB-${substring(0, 2)}:${substring(2, 4)}:${substring(4, 6)}"
                        }
                        Log.d(TAG,"이름: ${name}")
                        BluetoothHelper.startScan(name)
                    }
                    else ->toastShow("정확하지 않은 이름입니다. 다시 입력해 주세요.")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        captureManager = CaptureManager(this@ScannerActivity,viewDataBinding.bandQrScan).apply {
            initializeFromIntent(this@ScannerActivity.intent,savedInstanceState)
            decode()
        }
        viewDataBinding.bandQrScan?.let {
            it.decodeContinuous(mBarcodeCallback)
            it.setStatusText(applicationContext.getString(R.string.band_scan_text))
        }
    }

    private var mBarcodeCallback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            with(viewDataBinding) {
                etBandName.apply {
                    setText(result.toString())
                    setSelection(length())
                }
            }
        }
        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }
    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        BluetoothHelper.stopScan()
        captureManager.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        captureManager.onSaveInstanceState(outState)
    }
}