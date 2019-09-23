package com.rodrigmatrix.sigaaufc.ui.view.sigaa.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment.ClassesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents.DocumentsFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira.IraFragment
import kotlinx.android.synthetic.main.activity_sigaa.*

class SigaaActivity : AppCompatActivity() {

    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigaa)
        sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        sectionsPagerAdapter.addFragment(ClassesFragment())
        //sectionsPagerAdapter.addFragment(DocumentsFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 3
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_classes)
        //tabs.getTabAt(1)!!.setIcon(R.drawable.ic_description)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_assessment)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            confirmClose()
        }
        progress_sigaa.isVisible = false
    }

    private fun confirmClose(){
        runOnUiThread {
            MaterialAlertDialogBuilder(activity_sigaa.context)
                .setTitle("Encerrar Sessão")
                .setMessage("Deseja fechar o Sigaa e encerrar sua sessão? Será necessário efetuar login novamente")
                .setPositiveButton("Sim"){ _, _ ->
                    sectionsPagerAdapter.removeFragments()
                    this.finish()
                }
                .setNegativeButton("Não"){_, _ ->
                }
                .show()
        }
    }

    override fun onBackPressed() {
        confirmClose()
    }
}