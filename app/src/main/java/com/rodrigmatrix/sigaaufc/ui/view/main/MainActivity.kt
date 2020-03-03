package com.rodrigmatrix.sigaaufc.ui.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.navigation.findNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import com.igorronner.irinterstitial.services.PurchaseService
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.firebase.ERRO_ONBOARDING
import com.rodrigmatrix.sigaaufc.firebase.FINALIZOU_ONBOARDING
import com.rodrigmatrix.sigaaufc.firebase.PROFILE_BUTTON
import com.rodrigmatrix.sigaaufc.firebase.VISUALIZOU_ONBOARDING
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.internal.util.showProfileDialog
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.Version
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main2.profile_pic
import kotlinx.android.synthetic.main.app_bar_main2.profile_pic_card
import kotlinx.android.synthetic.main.fake_nav_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetSequence
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.FullscreenPromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal

class MainActivity : ScopedActivity(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: MainActivityViewModelFactory by instance()

    private val studentDao: StudentDao by instance()

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
        setContentView(R.layout.activity_main)
        val bundle = intent.extras
        if(bundle != null){
            val link = bundle.getString("link")
            val news = bundle.getString("newsId")
            val grade = bundle.getString("gradeId")
            when {
                link != null -> {
                    val newIntent = Intent(Intent.ACTION_VIEW)
                    newIntent.data = Uri.parse(link)
                    startActivity(newIntent)
                    finish()
                }
                news != null -> {
                    showDialogNews(news)
                }
                grade != null -> {
                    showDialogGrade(grade)
                }
                else -> {
                    if(!sigaaPreferences.showOnboarding()){
                        loadAd()
                    }
                }
            }
        }
        else{
            if(!sigaaPreferences.showOnboarding()){
                loadAd()
            }
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainActivityViewModel::class.java]
        launch(handler) {
            viewModel.getStudent().observe(this@MainActivity, androidx.lifecycle.Observer {student ->
                if(student == null) return@Observer
                bindUi(student)
            })
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_login,
                R.id.nav_notifications,
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
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()
        if(sigaaPreferences.showOnboarding()){
            try {
                showOnboarding()
            }
            catch(e: Exception){
                e.printStackTrace()
                events.addEvent(ERRO_ONBOARDING)
            }
        }
    }

    private fun showDialogNews(id: String) = launch {
        val news = withContext(Dispatchers.IO) {
            studentDao.getNewsWithIdAsync(id)
        }
        if(id == "test"){
            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Título: Teste")
                .setMessage("Conteúdo: Teste")
                .setPositiveButton("Ok") { i, _ ->
                    i.dismiss()
                }
                .show()
            return@launch
        }
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(news.title)
            .setMessage(news.content)
            .setPositiveButton("Ok") { i, _ ->
                i.dismiss()
            }
            .show()
    }

    private fun showDialogGrade(id: String) = launch {
        val grade = withContext(Dispatchers.IO) {
            studentDao.getGradeAsync(id)
        }
        if(id == "test"){
            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Nota de Teste")
                .setMessage("Nota: Teste")
                .setPositiveButton("Ok") { i, _ ->
                    i.dismiss()
                }
                .show()
            return@launch
        }
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(grade.name)
            .setMessage("Nota: ${grade.content}")
            .setPositiveButton("Ok") { i, _ ->
                i.dismiss()
            }
            .show()
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
        try {
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
        catch(e: Exception){
            e.printStackTrace()
        }
        return 0
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
            "ru" ->  navController.navigate(R.id.nav_ru)
            "library" -> navController.navigate(R.id.nav_library)
            "notifications" -> navController.navigate(R.id.nav_notifications)
        }
    }

    private fun loadAd(){
        if(sigaaPreferences.isPremium()){
            return
        }
        if(!IRAds.isPremium(this)){
            IRAds.newInstance(this).forceShowExpensiveInterstitial(false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindUi(student: Student){
        val profilePic = student.profilePic
        if(profilePic != "/sigaa/img/no_picture.png" && profilePic != ""){
            GlideApp.with(this)
                .load("https://si3.ufc.br/$profilePic")
                .into(profile_pic)
        }
        else{
            profile_pic.setImageResource(R.drawable.avatar_circle_blue)
        }
        profile_pic_card.setOnClickListener {
            events.addEvent(PROFILE_BUTTON)
           showProfileDialog(profile_pic_card, student)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun showOnboarding() {
        val backgroundColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(this@MainActivity, R.color.colorOnboarding), 0xE1)
        val promptFocalColor = Color.TRANSPARENT
        val tb = findViewById<MaterialToolbar>(R.id.toolbar)
        val promptFocal = CirclePromptFocal()
        events.addEvent(VISUALIZOU_ONBOARDING)
        MaterialTapTargetSequence()
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(R.id.start_onboarding)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setFocalRadius(20f)
                    .setPrimaryText(getString(R.string.welcome_title))
                    .setSecondaryText(getString(R.string.welcome_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(R.id.onboarding_profile)
                    .setCaptureTouchEventOnFocal(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptFocal(promptFocal)
                    .setPrimaryText(getString(R.string.profile_header_title))
                    .setSecondaryText(getString(R.string.profile_header_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(tb.getChildAt(1))
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(false)
                    .setAutoDismiss(false)
                    .setAutoFinish(false)
                    .setPromptStateChangeListener { prompt, state ->
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                            tb.getChildAt(1).performClick()
                            prompt.dismiss()
                        }
                    }
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptFocal(promptFocal)
                    .setPrimaryText(getString(R.string.nav_view_title))
                    .setSecondaryText(getString(R.string.nav_view_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(drawer_notifications)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(FullscreenPromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText(getString(R.string.onboarding_notifications_title))
                    .setSecondaryText(getString(R.string.onboarding_notifications_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(drawer_ru)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(FullscreenPromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText(getString(R.string.onboarding_ru_title))
                    .setSecondaryText(getString(R.string.onboarding_ru_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(drawer_about)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(FullscreenPromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText(getString(R.string.onboarding_about_title))
                    .setSecondaryText(getString(R.string.onboarding_about_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(drawer_settings)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(FullscreenPromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setPrimaryText(getString(R.string.onboarding_settings_title))
                    .setSecondaryText(getString(R.string.onboarding_settings_body))
            )
            .addPrompt(
                MaterialTapTargetPrompt.Builder(this@MainActivity)
                    .setTarget(R.id.start_onboarding)
                    .setCaptureTouchEventOnFocal(true)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setBackgroundColour(backgroundColor)
                    .setFocalColour(promptFocalColor)
                    .setTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setPromptBackground(RectanglePromptBackground())
                    .setPromptFocal(RectanglePromptFocal())
                    .setFocalRadius(20f)
                    .setPrimaryText("Por hoje é isso :)")
                    .setSecondaryText("Você pode visualizar esse tutorial sempre que quiser acessando as configurações. Por favor avalie o app na Google Play :)")
            )
            .setSequenceCompleteListener {
                drawer_layout.closeDrawers()
                sigaaPreferences.showOnboarding(false)
                events.addEvent(FINALIZOU_ONBOARDING)
            }
            .show()
    }
}
