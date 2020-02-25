package com.rodrigmatrix.sigaaufc.internal

import android.content.SharedPreferences

class CookiePreferences(
    private val sharedPreferences: SharedPreferences
) {

    fun getCookie() = sharedPreferences.getString("cookie", null)

    fun saveCookie(cookie: String){
        sharedPreferences.edit().putString("cookie", cookie).apply()
    }

}