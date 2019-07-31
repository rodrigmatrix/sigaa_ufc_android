package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.lifecycle.ViewModelProviders
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.IraFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance.AttendanceFragment
import kotlinx.android.synthetic.main.activity_sigaa.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ClassActivity : ScopedActivity(), KodeinAware {

    override val kodein by closestKodein()
    private lateinit var viewModel: ClassViewModel
    private val viewModelFactory: ClassViewModelFactory by instance()

    private var ongoing = false
    private var ongoingClass = false

    private lateinit var sectionsPagerAdapter: ClassPagerAdapter
    lateinit var idTurma: String
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigaa)
        sectionsPagerAdapter =
            ClassPagerAdapter(
                this,
                supportFragmentManager
            )
        viewModelFactory
        idTurma = intent.getStringExtra("idTurma")!!
        id = intent.getStringExtra("id")!!
        sectionsPagerAdapter.addFragment(AttendanceFragment())
        sectionsPagerAdapter.addFragment(GradesFragment.newInstance(idTurma, id))
        sectionsPagerAdapter.addFragment(IraFragment())
        sectionsPagerAdapter.addFragment(AttendanceFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 4
        val tabs: TabLayout = findViewById(R.id.tabs)
        title = "Disciplina"
        tabs.setupWithViewPager(viewPager)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ClassViewModel::class.java)
        setTabs()
        setClass()
    }



    private fun setTabs(){
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_attendance)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_assessment)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_download)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_news)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            loadClasses()
        }
    }

    private fun setClass(){
        launch(handler) {
            ongoingClass = true
            viewModel.setClass(id, idTurma)
            ongoingClass = false
        }
    }

    override fun onBackPressed(){
        loadClasses()
    }

    private fun loadClasses(){
        if(!ongoing && !ongoingClass){
            ongoing = true
            launch(handler) {
                val response = viewModel.fetchCurrentClasses()
                runOnUiThread {
                    sectionsPagerAdapter.removeFragments()
                    finish()
                }
                ongoing = false
            }
        }
    }
}