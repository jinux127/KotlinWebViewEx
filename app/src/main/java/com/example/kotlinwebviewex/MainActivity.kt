package com.example.kotlinwebviewex

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mWebView: WebView = findViewById(R.id.webView)
        mWebView.loadUrl("https://www.google.com")


        val mWebSettings = mWebView.settings


        mWebSettings.javaScriptEnabled = true //자바스크립트 허용
        mWebSettings.setSupportMultipleWindows(true) // 새창 띄우기 허용
        mWebSettings.javaScriptCanOpenWindowsAutomatically =true // 자바스크립트에서 자동으로 창을 열기 여부
        mWebSettings.loadWithOverviewMode =true // 메타태그 허용여부
        mWebSettings.useWideViewPort =true //화면 사이즈 맞추기 허용 여부
        mWebSettings.setSupportZoom(true) // 화면 줌 허용여부
        mWebSettings.builtInZoomControls = true// 화면 확대 축소 허용여부
        mWebSettings.displayZoomControls = true
        // mWebSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN //java에서는 setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN) 사용했음/
        mWebSettings.loadWithOverviewMode = true //컨텐츠 사이즈 맞추기
        mWebSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebSettings.domStorageEnabled = true;

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