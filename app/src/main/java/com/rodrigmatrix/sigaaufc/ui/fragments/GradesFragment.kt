package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rodrigmatrix.sigaaufc.R

class GradesFragment : Fragment() {
    lateinit var idTurma: String
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
