package com.rodrigmatrix.sigaaufc.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.AddCardActivity

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
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
    private lateinit var database: StudentsDatabase
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        database = Room.databaseBuilder(
            view!!.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        if(student.cardRU != ""){
            println("exibir dados")
        }
        else{
            println("exibir erro sem dados ru")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        add_card.setOnClickListener {
            val intent = Intent(context, AddCardActivity::class.java)
            this.startActivity(intent)
        }
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurante_universiario, container, false)
    }


}
