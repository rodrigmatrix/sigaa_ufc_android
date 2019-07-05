package com.rodrigmatrix.sigaaufc

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.rodrigmatrix.sigaaufc.persistence.Student
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: StudentsDatabase
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var student = database.studentDao().getStudent()
        if(student == null){
            database.studentDao().insertStudent(Student(0, "", "", "", "", "", "", "",
                "", "default", false, "", 0, "", "", ""))
        }
        else{
            when(student.theme){
                "light" -> {
                    setDefaultNightMode(MODE_NIGHT_NO)
                }
                "dark" -> {
                    setDefaultNightMode(MODE_NIGHT_YES)
                }
                "default" -> {
                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_login, R.id.nav_ru, R.id.nav_library,
                R.id.nav_settings, R.id.nav_about),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        when {
            student?.name != "" -> {
                navView.getHeaderView(0).student_name_menu_text.text = "Olá ${student.name.split(" ")[0]}"
                navView.getHeaderView(0).matricula_menu_text.text = "Matrícula: ${student.matricula}"
            }
            student?.nameRU != "" -> {
                navView.getHeaderView(0).student_name_menu_text.text = "Olá ${student.nameRU.split(" ")[0]}"
                navView.getHeaderView(0).matricula_menu_text.text = "Matrícula: ${student.matriculaRU}"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
