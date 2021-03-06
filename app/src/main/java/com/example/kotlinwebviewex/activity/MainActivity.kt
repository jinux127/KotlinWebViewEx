package com.example.kotlinwebviewex.activity

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.`interface`.CustomDialogInterface
import com.example.kotlinwebviewex.activity.base.BaseActivity
import com.example.kotlinwebviewex.databinding.ActivityMainBinding
import com.example.kotlinwebviewex.ext.*
import com.example.kotlinwebviewex.model.RxBusData
import com.example.kotlinwebviewex.model.base.BaseResponse
import com.example.kotlinwebviewex.utils.*
import com.example.kotlinwebviewex.utils.http.SenderManager.send
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.*
import kotlin.math.absoluteValue
import kotlin.properties.Delegates

class MainActivity : BaseActivity<ActivityMainBinding>(),SensorEventListener,CustomDialogInterface{
    override val layoutId: Int
        get() = R.layout.activity_main
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var permissionForResult: ActivityResultLauncher<Array<String>>
    val TAG = "tag"
    val sysLanguage = Locale.getDefault().language
    val sysCountry = Locale.getDefault().country
    class WebViewClientClass : WebViewClient(){
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView() {
        startForResult = activityResult()
        permissionForResult = permissionResult()

        main_wv.settings.apply {
            javaScriptEnabled = true // ?????????????????? ??????
            setSupportMultipleWindows(true) // ?????? ????????? ??????
            javaScriptCanOpenWindowsAutomatically = true // ???????????????????????? ???????????? ????????? ??????
            loadWithOverviewMode =true //???????????? ??????
            useWideViewPort  = true //?????? ????????? ?????????
            setSupportZoom(true)  // ???
            builtInZoomControls = true //?????? ?????? ?????? ??????
            displayZoomControls = true
            loadWithOverviewMode = true //????????? ????????? ?????????
            allowFileAccess = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            setGeolocationEnabled(true)
        }

        main_wv.webViewClient = WebClient()

        /*
        * WebView?????? javascript??? ??????????????? ?????????????????? alert??? ???????????? ?????????.
        * ?????????????????? ???????????? API?????? ????????????.
        * WebChromeClient?????? alert ????????? ???????????? onJSAlert??? ??????.
        * ?????? alert??? ?????? ???????????? ??????????????? ???????????? onJsAlert????????? ??????????????? ????????? ??? return ?????? true??? ???????????? ??????.
        * ?????? ???????????? ?????? ??????
        * */
        main_wv.webChromeClient = WebChromeClient()
        /*
        * WebView?????? ???????????????????????? ???????????? ?????? ??????
        * callback?.invoke(1. ???????????? Geolocation??? ??????????????? ???????????? ???????????? String
        *                 ,2. permission allow
        *                 ,3. WebView?????? ????????? ?????? ?????? ??????)*/
        main_wv.webChromeClient = object : WebChromeClient(){
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                Log.e(TAG,"origin: $origin")
                callback?.invoke(origin,true,true)
            }
        }

        main_wv.addJavascriptInterface(WebAppInterface(this),"Android")
        main_wv.loadUrl("http://192.168.0.102:8080/responsive/main")
//        main_wv.loadUrl("http://map.naver.com")


    }
    inner class WebAppInterface(private val mContext: Context) {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
//            val intent = Intent(this,ScannerActivity::class.java)
//            startActivity(intent)
        }
        @JavascriptInterface
        fun callScanner(toast: String) {
//            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
            RxBus.getSubject().onNext(
                RxBusData(
                    MainActivity::class.java.simpleName,
                    RXBUS_TYPE_MAIN_TO_BAND
                )
            )
//            ?????? ?????? ?????? ??????
//            qrScan()
        }
        @JavascriptInterface
        fun getAndroidLocale(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
            main_wv.post { main_wv.loadUrl("javascript:getLocale('$sysLanguage','$sysCountry')") }
        }
        @JavascriptInterface
        fun callDialog() {
            val customDialog = CustomDialog(this@MainActivity,this@MainActivity)
            customDialog.show()
        }
    }
    fun qrScan(){
        val integrator  = IntentIntegrator(this)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(false)
        integrator.setPrompt("QR????????? ??????????????????.")
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

    override fun initDataBinding() {
        compositeDisposable.add(
            RxBus.getSubject().subscribeWith(object : DisposableObserver<Any>() {
                override fun onNext(t: Any) {
                    if (t is RxBusData) {
                        when (t.type) {
                            RXBUS_TYPE_PERMISSION -> {
                                checkPermission(startForResult,permissionForResult)
                            }
                            RXBUS_TYPE_INTRO_TO_MAIN ->{
                                Handler(Looper.getMainLooper()).postDelayed({
//                                    intro.visibility = View.GONE
                                    main_wv.visibility = View.VISIBLE
                                }, 3000)
                            }
                            rxBus_type -> {
                                Log.e("tag","rxbus type = test")
                            }
                            RXBUS_TYPE_MAIN_TO_BAND->{
                                startActivity(Intent(this@MainActivity,ScannerActivity::class.java))
                            }
                            "1" ->{
                                Log.e("tag","rxbus type = 1 data = "+t.data)
                            }
                            "date" ->{
                                Log.e("tag","date = " + now())
                            }
                            else -> {
                            }
                        }

                    }
                    if (t is BaseResponse){
                        Log.e("tag","-????????? ?????? ????????? MainActivity?????? ??????-")
                        Log.e("tag","t.id: ${t.id}")
                        Log.e("tag","t.data: ${t.data}")
                    }
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            })

        )
    }

    override fun initAfter() {

        var rxBusData  = RxBusData("MainActivity",rxBus_type)
        RxBus.getSubject().onNext(rxBusData)
        var test2  = RxBusData("MainActivity","1","string")
        RxBus.getSubject().onNext(test2)
        var test3  = RxBusData("MainActivity","date")
        RxBus.getSubject().onNext(test3)
        RxBus.getSubject().onNext(RxBusData(MainActivity::class.java.simpleName, RXBUS_TYPE_PERMISSION))
//        createNoti(1,"??????","??? ??????????????????")
        createNoti2(2,"??????2","??????????????????.")
        send(this,jsonData="????????????????????? ????????? ????????? ??????????????????.")
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST)
    }
    /*
    * ???????????????
    * 1. SensorEventListner ??????
    * 2. SensorManager ??????
    * 3. sensor TYPE_LINEAR_ACCELERATION ??????
    * 4. ????????? ??????
    * 5. onSensorChanged event.value[i]??? ??????*/
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0].toInt().absoluteValue > 3||event.values[1].toInt().absoluteValue > 3||event.values[2].toInt().absoluteValue >3) {
                val dateTime: String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                } else ({
                    LocalDateTime.now()
                }).toString()
                Log.i("sensor","[time]: $dateTime     [x]: ${event.values[0].toInt()} [y]: ${event.values[1].toInt()} [z]: ${event.values[2].toInt()}")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.e("sensor","$sensor $accuracy")
    }

    override fun confirmClicked() {
        Log.d(TAG,"confirmtClicked")
    }

    override fun cancelClicked() {
        Log.d(TAG,"cancelClicked")
    }
}




