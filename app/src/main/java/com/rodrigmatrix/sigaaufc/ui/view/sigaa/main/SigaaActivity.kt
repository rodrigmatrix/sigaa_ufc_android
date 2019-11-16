package com.rodrigmatrix.sigaaufc.ui.view.sigaa.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.rodrigmatrix.sigaaufc.BuildConfig
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment.ClassesFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents.DocumentsFragment
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira.IraFragment
import kotlinx.android.synthetic.main.activity_sigaa.*

class SigaaActivity : AppCompatActivity() {

    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigaa)
        loadAd()
        sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        sectionsPagerAdapter.addFragment(ClassesFragment())
        //sectionsPagerAdapter.addFragment(DocumentsFragment())
        sectionsPagerAdapter.addFragment(IraFragment())
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = 3
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.getTabAt(0)!!.setIcon(R.drawable.ic_classes)
        //tabs.getTabAt(1)!!.setIcon(R.drawable.ic_description)
        tabs.getTabAt(1)!!.setIcon(R.drawable.ic_assessment)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            confirmClose()
        }
        progress_sigaa.isVisible = false
    }

    private fun confirmClose(){
        runOnUiThread {
            MaterialAlertDialogBuilder(activity_sigaa.context)
                .setTitle("Encerrar Sessão")
                .setMessage("Deseja fechar o Sigaa e encerrar sua sessão? Será necessário efetuar login novamente")
                .setPositiveButton("Sim"){ _, _ ->
                    sectionsPagerAdapter.removeFragments()
                    this.finish()
                }
                .setNegativeButton("Não"){_, _ ->
                }
                .show()
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

    override fun onBackPressed() {
        confirmClose()
    }
}