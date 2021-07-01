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
import java.util.*
import kotlin.properties.Delegates

class MainActivity : BaseActivity<ActivityMainBinding>(),SensorEventListener,CustomDialogInterface{
    override val layoutId: Int
        get() = R.layout.activity_main
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var permissionForResult: ActivityResultLauncher<Array<String>>
    val TAG = "MainActivity"
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
            javaScriptEnabled = true // 자바스크립트 허용
            setSupportMultipleWindows(true) // 새창 띄우기 허용
            javaScriptCanOpenWindowsAutomatically = true // 자바스크립트에서 자동으로 창열기 여부
            loadWithOverviewMode =true //메타태그 허용
            useWideViewPort  = true //화면 사이즈 맞추기
            setSupportZoom(true)  // 줌
            builtInZoomControls = true //화면 확대 축소 허용
            displayZoomControls = true
            loadWithOverviewMode = true //컨텐츠 사이즈 맞추기
            allowFileAccess = true
            loadsImagesAutomatically = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
            setGeolocationEnabled(true)
        }

        main_wv.webViewClient = WebClient()

        /*
        * WebView에서 javascript를 사용하도록 설정하더라도 alert은 동작하지 않는다.
        * 브라우저에서 지원하는 API이기 때문이다.
        * WebChromeClient에는 alert 발생시 동작하는 onJSAlert가 있다.
        * 혹시 alert를 다른 방식으로 표현해주기 위해서는 onJsAlert내부에 표현방식을 구현한 후 return 값만 true로 바꿔주면 된다.
        * 이를 이용하기 위해 설정
        * */
        main_wv.webChromeClient = WebChromeClient()
        /*
        * WebView에서 위치기반서비스를 사용하기 위한 설정
        * callback?.invoke(1. 웹뷰에서 Geolocation을 사용하려고 시도했던 콘텐츠의 String
        *                 ,2. permission allow
        *                 ,3. WebView보다 수명을 오래 유지 여부)*/
        main_wv.webChromeClient = object : WebChromeClient(){
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                callback?.invoke(origin,true,true)
            }
        }
//        main_wv.loadUrl("http://192.168.0.102:8080/responsive/main")

        main_wv.addJavascriptInterface(WebAppInterface(this),"Android")
//        main_wv.loadUrl("http://192.168.0.102:8080/responsive/main")
        main_wv.loadUrl("http://map.naver.com")


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
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
//            mContext.startActivity(Intent(mContext,ScannerActivity::class.java))
            qrScan()
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
                        Log.e("tag","-웹에서 받은 데이터 MainActivity에서 출력-")
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
//        createNoti(1,"연습","앱 실행중입니다")
        createNoti2(2,"연습2","연습중입니다.")
        send(this,jsonData="안드로이드에서 웹으로 전송할 데이터입니다.")
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST)
    }
    /*
    * 움직임감지
    * 1. SensorEventListner 상속
    * 2. SensorManager 등록
    * 3. sensor TYPE_LINEAR_ACCELERATION 생성
    * 4. 리스너 등록
    * 5. onSensorChanged event.value[i]로 출력*/
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (event.values[0].toInt() != 0||event.values[0].toInt() != 0||event.values[0].toInt() != 0) {
                Log.e("sensor","[x]: ${event.values[0].toInt()}")
                Log.e("sensor","[y]: ${event.values[1].toInt()}")
                Log.e("sensor","[z]: ${event.values[2].toInt()}")
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




