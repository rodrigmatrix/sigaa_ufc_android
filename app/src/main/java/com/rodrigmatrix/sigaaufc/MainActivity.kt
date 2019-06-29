package com.rodrigmatrix.sigaaufc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
        launch {
            val cookie = apiSigaa.getCookie()
            println("cookie $cookie")
            //apiSigaa.login(cookie, , )
        }
    }

}
