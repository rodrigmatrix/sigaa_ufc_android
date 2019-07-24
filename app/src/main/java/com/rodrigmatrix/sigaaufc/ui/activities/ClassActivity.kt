package com.rodrigmatrix.sigaaufc.ui.activities

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.adapters.ClassPagerAdapter
import com.rodrigmatrix.sigaaufc.ui.fragments.GradesFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.InfoFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.IraFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.MatriculaFragment
import kotlinx.android.synthetic.main.activity_sigaa.*

class ClassActivity : AppCompatActivity() {

    private lateinit var sectionsPagerAdapter: ClassPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigaa)
        sectionsPagerAdapter =
            ClassPagerAdapter(
                this,
                supportFragmentManager
            )
        sectionsPagerAdapter.addFragment(GradesFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        sectionsPagerAdapter.addFragment(MatriculaFragment())
        sectionsPagerAdapter.addFragment(InfoFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 4
        val tabs: TabLayout = findViewById(R.id.tabs)
        title = "Disciplina"
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_download)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_assessment)
        tabs.getTabAt(2)!!.setIcon(R.drawable.ic_news)
        tabs.getTabAt(3)!!.setIcon(R.drawable.ic_attendance)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            sectionsPagerAdapter.removeFragments()
            this.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sectionsPagerAdapter.removeFragments()
        this.finish()
    }
}