package com.techtap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyBinding
import com.techtap.databinding.ActivityTermsConditionBinding
import com.techtap.utils.Constant
import com.techtap.utils.Logger
import okhttp3.internal.userAgent

class TermsConditionActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityTermsConditionBinding
    private var url: String? = null


    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, TermsConditionActivity::class.java).apply {}.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_condition)
        initClick()
        init()
        initView()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initClick() {
        binding.onClickListener = this
    }

    private fun init() {
        url = BuildConfig.BASE_URL + "api/v2/" + Constant.URL_TERMS_AND_CONDITION
//        url = "https://www.testing.com/privacy-policy/"
        Logger.e("url = $url")
        binding.webView.isVerticalScrollBarEnabled = true

//        if (Build.VERSION.SDK_INT >= 21) {
//            binding.webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }

        binding.webView.loadUrl(url!!)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding.webView.settings.apply {
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(false)
            builtInZoomControls = true
            displayZoomControls = false
            domStorageEnabled = true
            databaseEnabled = true
            userAgentString = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36"
        }
        binding.webView.apply {
            setBackgroundColor(Color.WHITE)
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            isScrollbarFadingEnabled = false
            webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                    showHideProgress(true)
//                    return false
                    return if (url!!.startsWith("mailto")) {
                        try {
//                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        true
                    } else {
                        false
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, progress: Int) {
                    if (progress == 100) {
                        showHideProgress(false)
                    }
                }
            }
        }
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressClick()
        }
    }

    private fun onBackPressClick() {
        Logger.e("onBackPressClick = ${binding.webView.canGoBack()}")
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_back -> {
                onBackPressClick()
            }
        }
    }

}