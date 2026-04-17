package com.conchoback.haingon.ui.preview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.hideNavigation
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.databinding.ActivityPreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {
    private val viewModel: PreviewViewModel by viewModels()
    override fun setViewBinding(): ActivityPreviewBinding {
        return ActivityPreviewBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        lifecycleScope.launch {
            showLoading()
            initWebView()
            viewModel.setData(
                clothesType = intent.getStringExtra(IntentKey.CLOTHES_TYPE) ?: AssetsKey.SHIRT,
                path = intent.getStringExtra(IntentKey.INTENT_KEY) ?: ""
            )
        }
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch {
                combine(viewModel.clothesType, viewModel.clothesPath) { clothesType, clothesPath ->
                    Pair(clothesType, clothesPath)
                }.collect { (clothesType, clothesPath) -> setupUi(clothesType, clothesPath) }
            }
        }
    }

    override fun viewListener() {
        binding.actionBar.btnActionBarLeft.tap { handleBackLeftToRight() }

    }

    // Init
    //==================================================================================================================
    override fun initActionBar() = with(binding.actionBar) {
        setImageActionBar(btnActionBarLeft, R.drawable.ic_back)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() = with(binding) {
        // bật JS
        webView.settings.javaScriptEnabled = true

        // Asset loader (fix CORS)
        val assetLoader =
            WebViewAssetLoader.Builder().addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this@PreviewActivity))
                .build()

        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView, request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }

        // load index
        webView.loadUrl(AssetsKey.WEBVIEW_PREVIEW)
    }

    // Handle
    //==================================================================================================================

    // Observable
    //==================================================================================================================
    private fun setupUi(clothesType: String, clothesPath: String) {
        if (clothesType == "" || clothesPath == "") return

        lifecycleScope.launch(Dispatchers.IO) {
            val jvScrip = viewModel.sendImageFromPath(this@PreviewActivity)

            withContext(Dispatchers.Main) {
                if (jvScrip != "") {
                    delay(500)

                    runOnUiThread {
                        dLog("runOnUiThread")
                        binding.webView.evaluateJavascript(jvScrip, null)
                    }
                    dismissLoading()
                    hideNavigation()
                } else {
                    eLog("Lỗi con mẹ mày rồi")
                }
            }
        }
    }
    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================

}