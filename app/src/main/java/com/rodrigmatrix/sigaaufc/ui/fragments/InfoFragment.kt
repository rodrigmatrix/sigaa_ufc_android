package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.igorronner.irinterstitial.init.IRAds
import com.igorronner.irinterstitial.services.PurchaseService

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.data.repository.SigaaPreferences
import kotlinx.android.synthetic.main.fragment_info.*
import kotlinx.android.synthetic.main.premium_dialog.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class InfoFragment : Fragment(R.layout.fragment_info), KodeinAware {

    private var countPremium = 0
    override val kodein by closestKodein()
    private val sigaaPreferences: SigaaPreferences by instance()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        premium_button?.setOnClickListener {
            countPremium++
            if(countPremium == 10){
                countPremium = 0
                requestPremium()
            }
        }
        buy_premium_button?.setOnClickListener {
            PurchaseService(requireActivity()).purchase()
        }
    }

    override fun onResume() {
        if(IRAds.isPremium(requireContext())){
            buy_premium_button.visibility = View.GONE
            premium_message.visibility = View.GONE
        }
        else{
            buy_premium_button.visibility = View.VISIBLE
            premium_message.visibility = View.VISIBLE
        }
        super.onResume()
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

}
