package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades.GradesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.attendance.AttendanceFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.files.FilesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.NewsFragment
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
        sectionsPagerAdapter = ClassPagerAdapter(this, supportFragmentManager)
        idTurma = intent.getStringExtra("idTurma")!!
        id = intent.getStringExtra("id")!!
        sectionsPagerAdapter.addFragment(AttendanceFragment.newInstance(idTurma))
        sectionsPagerAdapter.addFragment(GradesFragment.newInstance(idTurma, id))
        sectionsPagerAdapter.addFragment(FilesFragment())
        sectionsPagerAdapter.addFragment(NewsFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 4
        val tabs: TabLayout = findViewById(R.id.tabs)
        title = "Disciplina"
        tabs.setupWithViewPager(viewPager)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ClassViewModel::class.java)
        setTabs()
        observeClass()
        setClass()
    }

    private fun observeClass(){
        launch {
            viewModel.getCurrentClass(idTurma).observe(this@ClassActivity, Observer {
                if(it == null) return@Observer
                title = it.name.split(" - ")[1]
            })
        }
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
            runOnUiThread {
                progress_sigaa.isVisible = false
            }
        }
    }

    override fun onBackPressed(){
        loadClasses()
    }

    private fun loadClasses(){
        if(!ongoing && !ongoingClass){
            ongoing = true
            launch(handler) {
                runOnUiThread {
                    progress_sigaa.isVisible = true
                }
                val response = viewModel.fetchCurrentClasses()
                runOnUiThread {
                    sectionsPagerAdapter.removeFragments()
                    finish()
                }
                ongoing = false
            }
        }
        else{
            runOnUiThread {
                Snackbar.make(activity_sigaa, "Aguarde...", Snackbar.LENGTH_LONG)
            }
        }
    }
}