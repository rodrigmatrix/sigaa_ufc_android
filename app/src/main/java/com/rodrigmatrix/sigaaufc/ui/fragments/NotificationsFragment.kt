package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.firebase.NOTIFICAR_TESTE
import com.rodrigmatrix.sigaaufc.internal.notification.sendFileNotification
import com.rodrigmatrix.sigaaufc.internal.notification.sendGradeNotification
import com.rodrigmatrix.sigaaufc.internal.notification.sendNewsNotification
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.kodein.di.generic.instance
import kotlin.random.Random

class NotificationsFragment: ScopedFragment(R.layout.fragment_notifications) {

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
        test_button.setOnClickListener {
            events.addEvent(NOTIFICAR_TESTE)
            when(Random.nextInt(0, 3)){
                0 -> {
                    requireContext().sendNewsNotification(
                        getString(R.string.news_notification_title, "Teste"),
                        getString(R.string.news_notification_body, "Teste", "Teste"),
                        "test"
                    )
                }
                1 -> {
                    requireContext().sendGradeNotification(
                        getString(R.string.grade_notification_title, "Teste"),
                        getString(R.string.grade_notification_body, "Teste"),
                        "test"
                    )
                }
                2 -> {
                    requireContext().sendFileNotification(
                        getString(R.string.file_notification_title, "Teste"),
                        getString(R.string.file_notification_body, "Teste")
                    )
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