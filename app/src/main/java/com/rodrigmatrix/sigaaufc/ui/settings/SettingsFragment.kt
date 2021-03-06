package com.rodrigmatrix.sigaaufc.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var preferences: SharedPreferences
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(com.rodrigmatrix.sigaaufc.R.xml.preferences)
        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, _ ->
            setTheme(pref.getString("THEME", null))
        }
        preferences.registerOnSharedPreferenceChangeListener(prefListener)

    }

    private fun setTheme(theme: String?){
        println("theme: $theme")
        when(theme){
            "LIGHT" -> setDefaultNightMode(MODE_NIGHT_NO)
            "DARK" -> setDefaultNightMode(MODE_NIGHT_YES)
            "BATTERY_SAVER" -> setDefaultNightMode(MODE_NIGHT_AUTO_BATTERY)
            "SYSTEM_DEFAULT" -> setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        }
        preferences.registerOnSharedPreferenceChangeListener(prefListener)
    }

}
