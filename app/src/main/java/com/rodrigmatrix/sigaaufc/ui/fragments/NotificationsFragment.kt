package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.kodein.di.generic.instance

class NotificationsFragment: ScopedFragment(R.layout.fragment_notifications) {

    private val sigaaPreferences: SigaaPreferences by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSwitch()
        switch_news.setOnCheckedChangeListener { _, isChecked ->
            sigaaPreferences.saveNewsNotification(isChecked)
        }
        switch_grades.setOnCheckedChangeListener { _, isChecked ->
            sigaaPreferences.saveGradesNotification(isChecked)
        }
        switch_files.setOnCheckedChangeListener { _, isChecked ->
            sigaaPreferences.saveFilesNotification(isChecked)
        }
        switch_notifications.setOnCheckedChangeListener { _, isChecked ->
            sigaaPreferences.saveAllNotification(isChecked)
            when(isChecked){
                true -> {
                    switch_news.isChecked = false
                    switch_grades.isChecked = false
                    switch_files.isChecked = false

                    switch_news.isEnabled = false
                    switch_grades.isEnabled = false
                    switch_files.isEnabled = false
                }
                false -> {
                    switch_news.isEnabled = true
                    switch_grades.isEnabled = true
                    switch_files.isEnabled = true
                }
            }
        }
    }

    private fun setSwitch(){
        switch_notifications.isChecked = sigaaPreferences.getAllNotification()
        switch_news.isChecked = sigaaPreferences.getNewsNotification()
        switch_grades.isChecked = sigaaPreferences.getGradesNotification()
        switch_files.isChecked = sigaaPreferences.getFilesNotification()
    }

}