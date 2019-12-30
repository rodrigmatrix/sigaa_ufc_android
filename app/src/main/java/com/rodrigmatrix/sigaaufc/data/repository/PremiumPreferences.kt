package com.rodrigmatrix.sigaaufc.data.repository

import android.content.SharedPreferences

class PremiumPreferences(
    private val sharedPreferences: SharedPreferences
) {

    fun isPremium(): Boolean = sharedPreferences.getBoolean("isPremium", false)

    fun isNotPremium(): Boolean = !isPremium()

    fun savePremium(isPremium: Boolean){
        sharedPreferences.edit().putBoolean("isPremium", isPremium).apply()
    }

}