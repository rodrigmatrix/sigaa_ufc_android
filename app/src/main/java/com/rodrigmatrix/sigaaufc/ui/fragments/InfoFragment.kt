package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import android.view.View
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.igorronner.irinterstitial.init.IRAds
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.firebase.COMPRAR_APP
import com.rodrigmatrix.sigaaufc.firebase.DESATIVAR_ANUNCIOS
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.premium_dialog.view.*


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
        val dialog = MaterialAlertDialogBuilder(requireContext())
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
