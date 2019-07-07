package com.rodrigmatrix.sigaaufc.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat

import com.rodrigmatrix.sigaaufc.R
import kotlinx.android.synthetic.main.fragment_library.*

class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        swipe_library.setColorSchemeResources(R.color.colorPrimary)
        swipe_library.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        swipe_library.isRefreshing = true
        library_webview.webViewClient = WebViewClient()
        library_webview.settings.javaScriptEnabled = true
        library_webview?.loadUrl("https://pergamum.ufc.br/pergamum/mobile/index.php")
        swipe_library.isRefreshing = false
        swipe_library.setOnRefreshListener {
            swipe_library.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            swipe_library.isRefreshing = true
            library_webview.reload()
            swipe_library.isRefreshing = false
        }
        super.onViewCreated(view, savedInstanceState)
    }


}
