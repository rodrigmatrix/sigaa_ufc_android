package com.rodrigmatrix.sigaaufc.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient

import com.rodrigmatrix.sigaaufc.R
import kotlinx.android.synthetic.main.fragment_library.*

/**
 * A simple [Fragment] subclass.
 */
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
        library_webview.webViewClient = WebViewClient()
        library_webview.settings.javaScriptEnabled = true
        library_webview?.loadUrl("https://pergamum.ufc.br/pergamum/mobile/index.php")
        super.onViewCreated(view, savedInstanceState)
    }

}
