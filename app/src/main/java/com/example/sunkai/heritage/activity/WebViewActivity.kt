package com.example.sunkai.heritage.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.example.sunkai.heritage.activity.base.BaseGlideActivity
import com.example.sunkai.heritage.databinding.ActivityWebViewBinding
import com.example.sunkai.heritage.value.URL

class WebViewActivity : BaseGlideActivity() {

    private lateinit var binding:ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.webView.loadUrl(intent.getStringExtra(URL) ?: return)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    binding.webviewProgress.progress = newProgress
                } else {
                    binding.webviewProgress.visibility = View.GONE
                }
            }
        }
        binding.webView.settings.javaScriptEnabled = true
    }
}
