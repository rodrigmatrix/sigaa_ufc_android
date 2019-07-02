package com.rodrigmatrix.sigaaufc

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

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var database: StudentsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_sigaa, R.id.nav_ru, R.id.nav_library,
                R.id.nav_settings, R.id.nav_about),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
//            when (student.theme) {
//                "light" -> {
//                    setDefaultNightMode(MODE_NIGHT_NO)
//                }
//                "dark" -> {
//                    setDefaultNightMode(MODE_NIGHT_YES)
//                }
//                "default" -> {
//                    setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
//                }
//            }
        }
        //setDefaultNightMode(MODE_NIGHT_NO)
        //setDefaultNightMode(MODE_NIGHT_YES)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
