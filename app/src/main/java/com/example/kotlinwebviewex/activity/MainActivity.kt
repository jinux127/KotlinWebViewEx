package com.example.kotlinwebviewex.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
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

    }
    class WebAppInterface(private val mContext:Context){
        @JavascriptInterface
        fun showToast(toast:String){
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