package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.fragments.IraFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.MatriculaFragment
import kotlinx.android.synthetic.main.fragment_sigaa.*

class SigaaFragment : Fragment() {
    lateinit var sectionsPagerAdapter: SigaaPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sigaa, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sectionsPagerAdapter = SigaaPagerAdapter(
            fragment_sigaa.context,
            childFragmentManager
        )
        sectionsPagerAdapter.addFragment(ClassesFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        sectionsPagerAdapter.addFragment(MatriculaFragment())
        tabs.setupWithViewPager(view_pager)
        view_pager.adapter = sectionsPagerAdapter
        view_pager.offscreenPageLimit = 3
    }
}
