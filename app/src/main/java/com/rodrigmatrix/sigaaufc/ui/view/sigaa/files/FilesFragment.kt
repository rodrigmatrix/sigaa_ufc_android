package com.rodrigmatrix.sigaaufc.ui.view.sigaa.files

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rodrigmatrix.sigaaufc.R

class FilesFragment : Fragment() {

    companion object {
        fun newInstance() = FilesFragment()
    }

    private lateinit var viewModel: FilesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.files_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
