package com.rodrigmatrix.sigaaufc.ui.activities

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.fragments.ClassesFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.IraFragment
import com.rodrigmatrix.sigaaufc.ui.fragments.MatriculaFragment
import com.rodrigmatrix.sigaaufc.ui.main.SectionsPagerAdapter

class ClassActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.addFragment(ClassesFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        sectionsPagerAdapter.addFragment(MatriculaFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 3
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}