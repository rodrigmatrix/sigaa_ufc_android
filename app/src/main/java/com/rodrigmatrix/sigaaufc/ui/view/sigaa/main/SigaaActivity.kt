package com.rodrigmatrix.sigaaufc.ui.view.sigaa.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.igorronner.irinterstitial.init.IRAds
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.internal.glide.GlideApp
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.ui.base.ScopedActivity
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment.ClassesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents.DocumentsFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira.IraFragment
import kotlinx.android.synthetic.main.activity_sigaa.*
import kotlinx.android.synthetic.main.activity_sigaa.toolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.lang.Exception

class SigaaActivity : ScopedActivity() {

    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private val sigaaPreferences: SigaaPreferences by instance()
    private val studentDao: StudentDao by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigaa)
        loadProfilePic()
        loadAd()
        sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        sectionsPagerAdapter.addFragment(ClassesFragment())
        sectionsPagerAdapter.addFragment(DocumentsFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 3
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)?.setIcon(R.drawable.ic_classes)
        tabs.getTabAt(1)?.setIcon(R.drawable.ic_description)
        tabs.getTabAt(2)?.setIcon(R.drawable.ic_assessment)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            confirmClose()
        }
        progress_sigaa.isVisible = false
    }

    private fun loadProfilePic() = launch{
        try {
            val profilePic = withContext(Dispatchers.IO) {
                studentDao.getStudentAsync().profilePic
            }
            if(profilePic != "/sigaa/img/no_picture.png"){
                GlideApp.with(this@SigaaActivity)
                    .load("https://si3.ufc.br/$profilePic")
                    .into(profile_pic)
            }
            else{
                profile_pic.setImageResource(R.drawable.avatar_circle_blue)
            }
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }

    private fun confirmClose(){
        runOnUiThread {
            MaterialAlertDialogBuilder(activity_sigaa.context)
                .setTitle("Encerrar Sessão")
                .setMessage("Deseja fechar o Sigaa e encerrar sua sessão? Será necessário efetuar login novamente")
                .setPositiveButton("Sim"){ i, _ ->
                    sectionsPagerAdapter.removeFragments()
                    i.dismiss()
                    finish()
                }
                .setNegativeButton("Não"){_, _ ->
                }
                .show()
        }
    }

    private fun loadAd(){
        val irAds = IRAds.newInstance(this)
        if(sigaaPreferences.isPremium()){
            return
        }
        if(!IRAds.isPremium(this)){
            irAds.forceShowExpensiveInterstitial(false)
        }
    }

    override fun onBackPressed() {
        confirmClose()
    }
}