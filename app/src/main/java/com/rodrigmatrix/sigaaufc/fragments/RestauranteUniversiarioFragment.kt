package com.rodrigmatrix.sigaaufc.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.AddCardActivity

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import kotlinx.android.synthetic.main.fragment_restaurante_universiario.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 */
class RestauranteUniversiarioFragment : Fragment(), CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurante_universiario, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apiSigaa = ApiSigaa()
        launch(handler) {
            var triple = apiSigaa.getRU("0421757", "0087438388")
            if(triple.first != "Success"){
                Snackbar.make(view, triple.first, Snackbar.LENGTH_LONG).show()
            }
            else{
                println(triple.third)
            }
        }
        add_card.setOnClickListener {
            val intent = Intent(context, AddCardActivity::class.java)
            this.startActivity(intent)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }

}
