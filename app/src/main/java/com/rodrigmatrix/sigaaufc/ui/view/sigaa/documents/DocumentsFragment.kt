package com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.documents_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class DocumentsFragment : ScopedFragment(), KodeinAware {

    private lateinit var viewModel: DocumentsViewModel

    override val kodein by closestKodein()
    private val viewModelFactory: DocumentsViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.documents_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(DocumentsViewModel::class.java)
        card_history.setOnClickListener {
            launch(handler) {

                viewModel.getHistorico()
            }
        }
    }

}
