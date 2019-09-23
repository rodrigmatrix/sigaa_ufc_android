package com.rodrigmatrix.sigaaufc.ui.view.sigaa.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

private var fragmentList = arrayListOf<Fragment>()
private val titleList = listOf( "Disciplinas", "IRA")

@Suppress("DEPRECATION")
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment){
        fragmentList.add(fragment)
    }

    fun removeFragments(){
        fragmentList.removeAll(fragmentList)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }

    override fun getCount(): Int {
        return titleList.size
    }
}