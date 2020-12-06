package com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class GradesFragment: ScopedFragment(R.layout.fragment_grades), KodeinAware {


    private lateinit var idTurma: String

    override val kodein by closestKodein()
    private lateinit var viewModel: GradesViewModel
    private val viewModelFactory: GradesViewModelFactory by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory)[GradesViewModel::class.java]
        idTurma = arguments?.getString("idTurma")!!
        println("idturma grade $idTurma")
        observeGrades()
    }

    private fun observeGrades(){
        launch {
            viewModel.deleteGrades()
            viewModel.fetchGrades(idTurma).observe(viewLifecycleOwner, Observer {
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

}
