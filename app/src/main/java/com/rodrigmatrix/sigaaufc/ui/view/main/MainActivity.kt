package com.rodrigmatrix.sigaaufc.ui.view.main

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.AppBarConfiguration
import androidx.preference.PreferenceManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class MainActivity : ScopedActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: MainActivityViewModelFactory by instance()

    private lateinit var viewModel: MainActivityViewModel


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var preferences: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        setTheme(preferences.getString("THEME", null))
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_login,
                R.id.nav_ru,
                R.id.nav_library,
                R.id.nav_settings,
                R.id.nav_about
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(MainActivityViewModel::class.java)
        launch(handler) {
            viewModel.getStudent().observe(this@MainActivity, androidx.lifecycle.Observer {student ->
                if(student == null) return@Observer
                if(student.profilePic != ""){
                    bindUi(student.profilePic, student.name, student.matricula)
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUi(profilePic: String, name: String, matricula: String){
        if(profilePic != "/sigaa/img/no_picture.png"){
            GlideApp.with(this@MainActivity)
                .load("https://si3.ufc.br/$profilePic")
                .into(nav_view.getHeaderView(0).profile_pic_image)
        }
        if(name != ""){
            nav_view.getHeaderView(0).student_name_menu_text.text = "Olá ${name.split(" ")[0]}"
            nav_view.getHeaderView(0).matricula_menu_text.text = "Matrícula: $matricula"
        }
    }

    private fun setTheme(theme: String?){
        when(theme){
            "LIGHT" -> setDefaultNightMode(MODE_NIGHT_NO)
            "DARK" -> setDefaultNightMode(MODE_NIGHT_YES)
            "BATTERY_SAVER" -> setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
            "SYSTEM_DEFAULT" -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
