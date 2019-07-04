package com.rodrigmatrix.sigaaufc.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import kotlinx.android.synthetic.main.fragment_sigaa.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import kotlin.coroutines.CoroutineContext


class SigaaFragment : Fragment(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sigaa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiSigaa = ApiSigaa()
        var cookie = ""
        launch(handler){
            cookie = apiSigaa.getCookie()
            progressLogin?.isVisible = false
            println("cookie $cookie")
        }
        login_btn.setOnClickListener {
            if(cookie != "") {
                progressLogin.isVisible = true
                var login = login_input.text.toString()
                var password = password_input.text.toString()
                launch{
                    var res = apiSigaa.login(cookie, login, password)
                    if(res != "Success"){
                        runOnUiThread {
                            Snackbar.make(main_activity, res, Snackbar.LENGTH_LONG).show()
                            progressLogin.isVisible = false
                        }
                    }
                    else{
                        runOnUiThread {
                            progressLogin.isVisible = false
                        }
                    }

                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

}
