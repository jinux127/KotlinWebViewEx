package com.example.kotlinwebviewex.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.kotlinwebviewex.R
import com.example.kotlinwebviewex.activity.base.BaseActivity
import com.example.kotlinwebviewex.databinding.ActivityMainBinding
import com.example.kotlinwebviewex.ext.*
import com.example.kotlinwebviewex.model.RxBusData
import com.example.kotlinwebviewex.utils.*
import com.example.kotlinwebviewex.utils.http.SenderManager.send
import io.reactivex.observers.DisposableObserver
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>(){

    override val layoutId: Int
        get() = R.layout.activity_main
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var permissionForResult: ActivityResultLauncher<Array<String>>

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
        main_wv.loadUrl("http://192.168.0.102:8080/responsive/main")

        main_wv.addJavascriptInterface(WebAppInterface(),"Android")
    }

    fun WebAppInterface() {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
            val intent = Intent(this,ScannerActivity::class.java)
            startActivity(intent)
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
        send(this,jsonData="내용내용내용내용내용내용내용내용내용내용내용내용")
    }

}