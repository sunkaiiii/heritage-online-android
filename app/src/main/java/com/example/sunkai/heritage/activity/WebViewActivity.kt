package com.example.sunkai.heritage.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.value.URL
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : BaseGlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.loadUrl(intent.getStringExtra(URL) ?: return)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    webviewProgress.progress = newProgress
                } else {
                    webviewProgress.visibility = View.GONE
                }
            }
        }
        webView.settings.javaScriptEnabled = true
    }
}
