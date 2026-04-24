package com.sakuralaaa.gptimagestudio

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.activity.addCallback

class MainActivity : AppCompatActivity() {
    private lateinit var urlInput: EditText
    private lateinit var authKeyInput: EditText
    private lateinit var saveButton: Button
    private lateinit var openButton: Button
    private lateinit var settingsButton: ImageButton
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var settingsPanel: View

    private val prefs by lazy {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        urlInput = findViewById(R.id.urlInput)
        authKeyInput = findViewById(R.id.authKeyInput)
        saveButton = findViewById(R.id.saveButton)
        openButton = findViewById(R.id.openButton)
        settingsButton = findViewById(R.id.settingsButton)
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        settingsPanel = findViewById(R.id.settingsPanel)

        urlInput.setText(prefs.getString(KEY_URL, ""))
        authKeyInput.setText(prefs.getString(KEY_AUTH, ""))

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                progressBar.isVisible = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progressBar.isVisible = false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val nextUrl = request?.url?.toString().orEmpty()
                if (nextUrl.isBlank()) {
                    return true
                }
                view?.loadUrl(nextUrl, authHeaders())
                return true
            }
        }

        saveButton.setOnClickListener {
            saveConfig()
        }

        openButton.setOnClickListener {
            saveConfig()
            openConfiguredSite()
        }

        settingsButton.setOnClickListener {
            settingsPanel.isVisible = !settingsPanel.isVisible
        }

        onBackPressedDispatcher.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

        if (urlInput.text.isNotBlank()) {
            openConfiguredSite()
            settingsPanel.isVisible = false
        }
    }

    private fun saveConfig() {
        prefs.edit()
            .putString(KEY_URL, urlInput.text.toString().trim())
            .putString(KEY_AUTH, authKeyInput.text.toString().trim())
            .apply()
    }

    private fun openConfiguredSite() {
        val url = urlInput.text.toString().trim()
        if (url.isBlank()) {
            urlInput.error = getString(R.string.url_required)
            return
        }

        webView.loadUrl(normalizeUrl(url), authHeaders())
    }

    private fun authHeaders(): Map<String, String> {
        val authKey = authKeyInput.text.toString().trim()
        return if (authKey.isBlank()) {
            emptyMap()
        } else {
            mapOf(AUTH_HEADER to authKey)
        }
    }

    private fun normalizeUrl(raw: String): String {
        return if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            "https://$raw"
        }
    }

    companion object {
        private const val PREFS_NAME = "gpt_image_studio"
        private const val KEY_URL = "project_url"
        private const val KEY_AUTH = "oauth_key"
        private const val AUTH_HEADER = "Authorization"
    }
}
