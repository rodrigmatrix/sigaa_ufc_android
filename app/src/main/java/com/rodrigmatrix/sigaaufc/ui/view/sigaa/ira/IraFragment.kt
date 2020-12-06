package com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_ira.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class IraFragment : ScopedFragment(R.layout.fragment_ira), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: IraViewModelFactory by instance()

    private lateinit var viewModel: IraViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[IraViewModel::class.java]
        observeIra()
    }

    private fun observeIra(){
        launch {
            viewModel.getIra().observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                recycler_view_ira.layoutManager = LinearLayoutManager(context)
                recycler_view_ira.adapter = IraAdapter(it)
            })
        }
    }

}
