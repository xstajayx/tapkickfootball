package com.example.tapkickfootball

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var rewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)

        webView = findViewById(R.id.gameWebView)
        setupWebView()

        findViewById<AdView>(R.id.bannerAdView).loadAd(AdRequest.Builder().build())
        loadRewardedAd()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        webView.loadUrl("file:///android_asset/index.html")
    }

    private fun loadRewardedAd() {
        RewardedAd.load(
            this,
            getString(R.string.admob_rewarded_id),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            rewardedAd = null
                            loadRewardedAd()
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                            rewardedAd = null
                            loadRewardedAd()
                        }
                    }
                }

                override fun onAdFailedToLoad(p0: com.google.android.gms.ads.LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    private fun notifyRewardGranted(type: String) {
        val script = "window.onAndroidRewardGranted && window.onAndroidRewardGranted('$type');"
        webView.evaluateJavascript(script, null)
    }

    private fun notifyRewardUnavailable() {
        val script = "window.onAndroidRewardUnavailable && window.onAndroidRewardUnavailable();"
        webView.evaluateJavascript(script, null)
    }

    inner class AndroidBridge {
        @JavascriptInterface
        fun showRewardedRevive() {
            runOnUiThread {
                val ad = rewardedAd
                if (ad == null) {
                    notifyRewardUnavailable()
                    loadRewardedAd()
                    return@runOnUiThread
                }

                ad.show(this@MainActivity) {
                    notifyRewardGranted("revive")
                }
            }
        }
    }

    override fun onDestroy() {
        webView.removeJavascriptInterface("AndroidBridge")
        webView.destroy()
        super.onDestroy()
    }
}
