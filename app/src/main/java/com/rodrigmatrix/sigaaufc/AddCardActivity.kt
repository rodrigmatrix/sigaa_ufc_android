package com.rodrigmatrix.sigaaufc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AddCardActivity : AppCompatActivity(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            this.finish()
        }
        add_card_button.setOnClickListener {
            launch(handler){
                val api = ApiSigaa()
                if(isValid()){

                }
            }
        }

    }
    private fun isValid(): Boolean{
        if(card_number_input.){

        }
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
}
