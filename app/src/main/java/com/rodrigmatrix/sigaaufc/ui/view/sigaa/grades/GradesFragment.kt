package com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.view.ClassesViewModel
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.view.ClassesViewModelFactory
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

class GradesFragment: Fragment(), KodeinAware, CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private lateinit var idTurma: String

    override val kodein by closestKodein()
    private lateinit var viewModel: GradesViewModel
    private val viewModelFactory: GradesViewModelFactory by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        job = Job()
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(GradesViewModel::class.java)
        idTurma = arguments?.getString("idTurma")!!
        println("idturma grade $idTurma")
        observeGrades()
    }

    private fun observeGrades(){
        job = launch {
            viewModel.deleteGrades()
            viewModel.fetchGrades(idTurma).observe(this@GradesFragment, Observer {
                if(it == null) return@Observer
                println(it)
                println(idTurma)
                runOnUiThread {
                    recycler_view_grades?.layoutManager = LinearLayoutManager(context)
                    recycler_view_grades?.adapter = GradesAdapter(it)
                }
            })
        }

    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades, container, false)
    }
}
