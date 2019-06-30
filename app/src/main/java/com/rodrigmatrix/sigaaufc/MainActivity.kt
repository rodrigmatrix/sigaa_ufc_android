package com.rodrigmatrix.sigaaufc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val apiSigaa = ApiSigaa()
        var cookie = ""
        var loginR = "rodrigmatrix"
        var passwordR = "iphone5s"
        launch(handler){
            cookie = apiSigaa.getCookie()
            progressLogin?.isVisible = false
            println("cookie $cookie")
        }
        login_btn.setOnClickListener {
            if(cookie != "") {
                progressLogin.isVisible = true
                val login = login_input.text.toString()
                val password = password_input.text.toString()
                launch(handler) {
                    apiSigaa.login(cookie, loginR, passwordR)
                    progressLogin.isVisible = false
                }
            }
        }
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

}
