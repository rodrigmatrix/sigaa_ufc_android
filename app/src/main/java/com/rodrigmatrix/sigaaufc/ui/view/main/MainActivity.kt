package com.rodrigmatrix.sigaaufc.ui.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.rodrigmatrix.sigaaufc.BuildConfig
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
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
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var navController: NavController

    @SuppressLint("SetTextI18n", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val bundle = intent.extras
        if(bundle != null){
            val link = bundle.getString("link")
            if(link != null){
                val newIntent = Intent(Intent.ACTION_VIEW)
                newIntent.data = Uri.parse(link)
                startActivity(newIntent)
            }
        }
        setContentView(R.layout.activity_main)
        loadAd()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
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
        getShortcut()
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

    private fun getShortcut(){
        when(intent.extras?.getString("shortcut")){
            "ru" -> {
                navController.navigate(R.id.nav_ru)
            }
            "library" -> {
                navController.navigate(R.id.nav_library)
            }
        }
    }

    private fun loadAd(){
        MobileAds.initialize(this){}
        var adUnitInterstitial = getString(R.string.ad_unit_interstitial)
        val adRequest = AdRequest.Builder()
        if(BuildConfig.DEBUG){
            adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
            adUnitInterstitial = "ca-app-pub-3940256099942544/1033173712"
        }
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = adUnitInterstitial
        mInterstitialAd.loadAd(adRequest.build())
        mInterstitialAd.adListener = object: AdListener() {
            override fun onAdLoaded() {
                mInterstitialAd.show()
            }
        }
        val adRequestBanner = AdRequest.Builder()
        if(BuildConfig.DEBUG){
            adRequestBanner.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        }
        adView?.loadAd(adRequestBanner.build())
    }


    @SuppressLint("SetTextI18n")
    private fun bindUi(profilePic: String, name: String, matricula: String){
        if(profilePic != "/sigaa/img/no_picture.png"){
            GlideApp.with(this@MainActivity)
                .load("https://si3.ufc.br/$profilePic")
                .into(nav_view.getHeaderView(0).profile_pic_image)
        }
        else{
            nav_view.getHeaderView(0).profile_pic_image.setImageResource(R.drawable.avatar_circle_blue)
        }
        if(name != ""){
            nav_view.getHeaderView(0).student_name_menu_text.text = "Olá ${name.split(" ")[0]} ${name.split(" ").last()}"
            nav_view.getHeaderView(0).matricula_menu_text.text = "Matrícula: $matricula"
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
