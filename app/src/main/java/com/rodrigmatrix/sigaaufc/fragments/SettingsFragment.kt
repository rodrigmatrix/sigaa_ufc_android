package com.rodrigmatrix.sigaaufc.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.activity_add_card.*
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {
    lateinit var database: StudentsDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Room.databaseBuilder(
            view!!.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        var selected = 1
        when(student.theme){
            "light" -> {
                selected = 0
                selected_theme_text.text = "Light"
            }
            "dark" -> {
                selected = 1
                selected_theme_text.text = "Dark"
            }
            "default" -> {
                selected = 2
                selected_theme_text.text = "Automático"
            }
        }
        var items = resources.getStringArray(R.array.theme_array)
        theme_button.setOnClickListener {
            MaterialAlertDialogBuilder(settings_fragment.context)
                .setTitle("Selecione o tema")
                .setSingleChoiceItems(items, selected){ dialog, item ->
                    when(item){
                        0 -> {
                            student.theme = "light"
                            selected = 0
                            selected_theme_text.text = "Light"
                            database.studentDao().insertStudent(student)
                            setDefaultNightMode(MODE_NIGHT_NO)
                        }
                        1 -> {
                            student.theme = "dark"
                            selected_theme_text.text = "Dark"
                            selected = 1
                            database.studentDao().insertStudent(student)
                            setDefaultNightMode(MODE_NIGHT_YES)
                        }
                        2 -> {
                            student.theme = "default"
                            selected = 2
                            selected_theme_text.text = "Automático"
                            database.studentDao().insertStudent(student)
                            setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)

                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

}
