package com.rodrigmatrix.sigaaufc.ui.view.sigaa.files

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.File
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesAdapter
import kotlinx.android.synthetic.main.fragment_files.*
import kotlinx.android.synthetic.main.fragment_grades.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FilesFragment : ScopedFragment(R.layout.fragment_files), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: FilesViewModelFactory by instance()

    private lateinit var viewModel: FilesViewModel
    private lateinit var idTurma: String
    private lateinit var cookie: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[FilesViewModel::class.java]
        idTurma = arguments?.getString("idTurma")!!
        bindUI()
    }

    private fun bindUI(){
        launch(handler) {
            cookie = viewModel.getCookie()
            viewModel.deleteFiles(idTurma)
            viewModel.getFiles(idTurma).observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                if(it.size == 1){
                    if(it[0].name == "null"){
                        runOnUiThread {
                            no_file?.isVisible = true
                            recycler_view_files?.isVisible = false
                        }
                    }
                    else{
                        setFiles(it)
                    }
                }
                else{
                    setFiles(it)
                }
            })
        }
    }

    private fun setFiles(files: MutableList<File>){
        runOnUiThread {
            no_file?.isVisible = false
            recycler_view_files?.isVisible = true
            recycler_view_files?.layoutManager = LinearLayoutManager(context)
            recycler_view_files?.adapter = FileAdapter(files, cookie)
        }
    }

}
