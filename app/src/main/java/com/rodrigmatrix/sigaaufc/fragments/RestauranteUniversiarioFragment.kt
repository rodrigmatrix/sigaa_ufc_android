package com.rodrigmatrix.sigaaufc.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.AddCardActivity

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.adapters.RestauranteUniversitarioAdapter
import com.rodrigmatrix.sigaaufc.api.ApiSigaa
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.fragment_restaurante_universiario.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
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

    override fun onDestroyView() {
        super.onDestroyView()
        println("limpou backstack")
    }

    @SuppressLint("SetTextI18n")
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
            card_view_ru?.isVisible = true
            card_holder_text?.text = student.nameRU
            credits_text?.text = "${student.creditsRU} créditos"
            recyclerView_ru?.layoutManager = LinearLayoutManager(context)
            recyclerView_ru?.adapter = RestauranteUniversitarioAdapter(database.studentDao().getHistoryRU())
            ru_refresh?.isVisible = true
            no_card?.isVisible = false
            ru_refresh?.isRefreshing = false
        }
        else{
            card_view_ru.isVisible = false
            ru_refresh?.isVisible = false
            no_card?.isVisible = true
            ru_refresh?.isRefreshing = false
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ru_refresh.setColorSchemeResources(R.color.colorPrimary)
        ru_refresh.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(view.context, R.color.colorSwipeRefresh))
        add_card.setOnClickListener {
            val intent = Intent(context, AddCardActivity::class.java)
            this.startActivity(intent)
        }
        database = Room.databaseBuilder(
            view.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        loadData()
        ru_refresh?.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData(){
        launch(handler){
            runOnUiThread {
                ru_refresh.isRefreshing = true
            }
            val api = ApiSigaa()
            var student = database.studentDao().getStudent()
            if(student.cardRU != ""){
                var triple = api.getRU(student.cardRU, student.matriculaRU)
                if(triple.first == "Success"){
                    database.studentDao().deleteHistoryRU()
                    triple.third.forEach {
                        database.studentDao().insertRU(it)
                    }
                    runOnUiThread {
                        recyclerView_ru?.layoutManager = LinearLayoutManager(context)
                        recyclerView_ru?.adapter = RestauranteUniversitarioAdapter(database.studentDao().getHistoryRU())
                        ru_refresh?.isRefreshing = false
                    }
                }
                else{
                    runOnUiThread {
                        Snackbar.make(add_card_activity, triple.first, Snackbar.LENGTH_LONG).show()
                        ru_refresh?.isRefreshing = false
                    }
                }
            }
        }
    }
    private val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
        ru_refresh?.isRefreshing = false
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurante_universiario, container, false)
    }


}
