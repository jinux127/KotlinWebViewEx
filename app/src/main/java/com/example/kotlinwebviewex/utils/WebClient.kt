package com.example.kotlinwebviewex.utils

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class WebClient : WebViewClient() {
    override fun onReceivedError(
        view: WebView?, request: WebResourceRequest?, error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error) //error 페이지
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url) // 페이지가 끝날때 1번 호출
    }

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        view!!.loadUrl(url!!)
        return true
    }
}