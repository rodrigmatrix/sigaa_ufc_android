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

    fun saveAllNotification(isEnabled: Boolean){
        sharedPreferences.edit().putBoolean("all_notifications", isEnabled).apply()
    }

    fun getAllNotification() = sharedPreferences.getBoolean("all_notifications", false)


    fun saveNewsNotification(isEnabled: Boolean){
        sharedPreferences.edit().putBoolean("news_notifications", isEnabled).apply()
    }

    fun getNewsNotification() = sharedPreferences.getBoolean("news_notifications", false)

    fun saveFilesNotification(isEnabled: Boolean){
        sharedPreferences.edit().putBoolean("files_notifications", isEnabled).apply()
    }

    fun getFilesNotification() = sharedPreferences.getBoolean("files_notifications", false)

    fun saveGradesNotification(isEnabled: Boolean){
        sharedPreferences.edit().putBoolean("grades_notifications", isEnabled).apply()
    }

    fun getGradesNotification() = sharedPreferences.getBoolean("grades_notifications", false)

}