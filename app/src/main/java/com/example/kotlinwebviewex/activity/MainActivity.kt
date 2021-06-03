package com.example.kotlinwebviewex.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinwebviewex.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mWebView: WebView = findViewById(R.id.webView)
        mWebView.loadUrl("http://192.168.0.102:8080/responsive/main")


        val mWebSettings = mWebView.settings

        mWebSettings.apply {
            javaScriptEnabled = true // 자바스크립트 허용
            setSupportMultipleWindows(true) // 새창 띄우기 허용
            javaScriptCanOpenWindowsAutomatically = true // 자바스크립트에서 자동으로 창열기 여부
            loadWithOverviewMode =true //메타태그 허용
            useWideViewPort  = true //화면 사이즈 맞추기
            setSupportZoom(true)  // 줌
            builtInZoomControls = true //화면 확대 축소 허용
            displayZoomControls = true
            loadWithOverviewMode = true //컨텐츠 사이즈 맞추기
            cacheMode = WebSettings.LOAD_NO_CACHE
            domStorageEnabled = true
        }

        mWebView.addJavascriptInterface(WebAppInterface(this),"Android") //Javascript 설정
        mWebView.webViewClient = WebViewClientClass()

        /*
        * WebView에서 javascript를 사용하도록 설정하더라도 alert은 동작하지 않는다.
        * 브라우저에서 지원하는 API이기 때문이다.
        * WebChromeClient에는 alert 발생시 동작하는 onJSAlert가 있다.
        * 혹시 alert를 다른 방식으로 표현해주기 위해서는 onJsAlert내부에 표현방식을 구현한 후 return 값만 true로 바꿔주면 된다.
        * 이를 이용하기 위해 설정
        * */
        mWebView.webChromeClient = WebChromeClient()

    }

    class WebAppInterface(private val mContext:Context){
        @JavascriptInterface
        fun showToast(toast:String){
            Log.d("111","눌림")
            Toast.makeText(mContext,toast, Toast.LENGTH_SHORT).show()
        }
    }

    class WebViewClientClass : WebViewClient(){
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }
}