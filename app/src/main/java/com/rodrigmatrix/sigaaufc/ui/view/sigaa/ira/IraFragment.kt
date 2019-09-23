package com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_ira.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class IraFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: IraViewModelFactory by instance()

    private lateinit var viewModel: IraViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ira, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(IraViewModel::class.java)
        observeIra()
    }

    private fun observeIra(){
        launch {
            viewModel.getIra().observe(this@IraFragment, Observer {
                if(it == null) return@Observer
                recycler_view_ira.layoutManager = LinearLayoutManager(context)
                recycler_view_ira.adapter = IraAdapter(it)
            })
        }
    }

}
