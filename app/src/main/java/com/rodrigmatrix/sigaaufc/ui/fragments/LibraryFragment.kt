package com.rodrigmatrix.sigaaufc.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat

import com.rodrigmatrix.sigaaufc.R
import kotlinx.android.synthetic.main.fragment_library.*

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipe_library.setColorSchemeResources(R.color.colorPrimary)
        swipe_library.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        loadPage()
        swipe_library.setOnRefreshListener {
            swipe_library.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            library_webview.reload()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPage(){
        swipe_library.isRefreshing = true
        library_webview.webViewClient = object: WebViewClient(){
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                swipe_library.isRefreshing = true
            }
            override fun onPageFinished(view: WebView, url: String) {
                swipe_library.isRefreshing = false
            }
        }
        library_webview.settings.javaScriptEnabled = true
        library_webview?.loadUrl("https://pergamum.ufc.br/pergamum/mobile/index.php")
    }
}
