package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.android.billingclient.api.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.igorronner.irinterstitial.dto.IRSkuDetails
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.services.*

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import com.rodrigmatrix.sigaaufc.firebase.COMPRAR_APP
import com.rodrigmatrix.sigaaufc.firebase.DESATIVAR_ANUNCIOS
import com.rodrigmatrix.sigaaufc.internal.PremiumService
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.premium_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class InfoFragment : ScopedFragment(R.layout.fragment_info), PurchasesUpdatedListener {

    private var countPremium = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        premium_button?.setOnClickListener {
            events.addEvent(DESATIVAR_ANUNCIOS)
            countPremium++
            if(countPremium == 10){
                countPremium = 0
                requestPremium()
            }
        }
        //PremiumService(requireContext(), this).checkIsPremium()
        buy_premium_button?.setOnClickListener {
            events.addEvent(COMPRAR_APP)

        }
    }

    override fun onResume() {
        checkIsPremium()
        super.onResume()
    }

    private fun checkIsPremium(){
        if(IRAds.isPremium(requireContext())){
            buy_premium_button.visibility = View.GONE
            premium_message.visibility = View.GONE
        }
        else{
            buy_premium_button.visibility = View.GONE
            premium_message.visibility = View.GONE
        }
    }

    private fun requestPremium(){
        val layout = layoutInflater.inflate(R.layout.premium_dialog, null)
        val dialog = MaterialAlertDialogBuilder(context)
            .setView(layout)
            .show()
        layout.validate_button?.setOnClickListener {
            if(layout.premium_input.text.toString().toInt() == 5){
                sigaaPreferences.savePremium(true)
            }
            else{
                sigaaPreferences.savePremium(false)
            }
            dialog.dismiss()
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {

    }

}
