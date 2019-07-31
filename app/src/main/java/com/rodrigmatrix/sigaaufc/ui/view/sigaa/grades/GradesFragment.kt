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
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class GradesFragment : ScopedFragment(), KodeinAware {

    lateinit var idTurma: String
    lateinit var id: String

    override val kodein by closestKodein()
    private lateinit var viewModel: GradesViewModel
    private val viewModelFactory: GradesViewModelFactory by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(GradesViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeGrades()
    }

    private fun observeGrades(){
        launch {
            viewModel.fetchGrades(idTurma).observe(this@GradesFragment, Observer {
                if(it == null) return@Observer
                runOnUiThread {
                    recycler_view_grades?.layoutManager = LinearLayoutManager(context)
                    recycler_view_grades?.adapter = GradesAdapter(it)
                }
            })
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grades, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(idTurmaValue: String, idValue: String) =
            GradesFragment().apply {
                arguments = Bundle().apply {
                    idTurma = idTurmaValue
                    id = idValue
                }
            }
    }
}
