package com.rodrigmatrix.sigaaufc.ui.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransformSharedElementCallback
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.igorronner.irinterstitial.dto.IRSkuDetails
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.services.ProductPurchasedListener
import com.igorronner.irinterstitial.services.ProductsListListener
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.network.SigaaApi
import com.rodrigmatrix.sigaaufc.data.repository.PremiumPreferences
import com.rodrigmatrix.sigaaufc.firebase.RemoteConfig
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.persistence.entity.Version
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import retrofit2.HttpException
import java.lang.Exception

class MainActivity : ScopedActivity(), KodeinAware, ProductsListListener, ProductPurchasedListener {

    override val kodein by closestKodein()
    private val viewModelFactory: MainActivityViewModelFactory by instance()
    private val remoteConfig: RemoteConfig by instance()
    private val premiumPreferences: PremiumPreferences by instance()
    private val sigaaApi: SigaaApi by instance()

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var navController: NavController
    val UPDATE_REQUEST_CODE = 400
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            @SuppressLint("SwitchIntDef")
            override fun onStateUpdate(installState: InstallState) {
                when(installState.installStatus()) {
                    InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(this)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if(bundle != null){
            val link = bundle.getString("link")
            if(link != null){
                val newIntent = Intent(Intent.ACTION_VIEW)
                newIntent.data = Uri.parse(link)
                startActivity(newIntent)
                setContentView(R.layout.activity_main)
            }
            else{
                setContentView(R.layout.activity_main)
                loadAd()
            }
        }
        else{
            setContentView(R.layout.activity_main)
            loadAd()
        }
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
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.main_view),
            "Update pronto para instalar.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("INSTALAR") { appUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            show()
        }
    }

    private fun checkForUpdates(){
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        val versions = remoteConfig.getVersions()
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateType = checkUpdateType(appUpdateInfo.availableVersionCode(), versions)
            if (appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(updateType)){
                if(updateType == AppUpdateType.FLEXIBLE){
                    appUpdateManager.registerListener(appUpdatedListener)
                }
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateType,
                    this,
                    UPDATE_REQUEST_CODE
                )
            }
        }
    }

    private fun checkUpdateType(availableVersionCode: Int, versions: List<Version>): Int{
        var updateType = 0
        versions.forEach {
            if(availableVersionCode == it.versionCode){
                updateType = it.updateType
            }
        }
        if(updateType !in 0..1){
            return 0
        }
        return updateType
    }

    override fun onResume() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
        super.onResume()
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
        if(premiumPreferences.isNotPremium() || !IRAds.isPremium(this)){
            IRAds.newInstance(this).forceShowExpensiveInterstitial(false)
        }
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

    override fun onProductList(list: List<IRSkuDetails>) {
        list.forEach { sku ->
            if (sku.sku == "premium") {
                //weeksPremiumPrice.text = sku.price
            }
        }
    }

    override fun onProductsPurchased() {
//
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
