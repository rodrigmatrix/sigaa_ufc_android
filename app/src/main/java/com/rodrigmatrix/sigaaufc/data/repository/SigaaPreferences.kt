package com.rodrigmatrix.sigaaufc.data.repository

import android.content.SharedPreferences

class SigaaPreferences(
    private val sharedPreferences: SharedPreferences
) {

    fun isPremium(): Boolean = sharedPreferences.getBoolean("isPremium", false)

    fun isNotPremium(): Boolean = !isPremium()

    fun savePremium(isPremium: Boolean){
        sharedPreferences.edit().putBoolean("isPremium", isPremium).apply()
    }

    fun saveLastVinculo(id: String){
        sharedPreferences.edit().putString("lastVinculo", id).apply()
    }

    fun getLastVinculo() = sharedPreferences.getString("lastVinculo", "1")

}