package com.rodrigmatrix.sigaaufc.firebase

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.Version
import com.rodrigmatrix.sigaaufc.persistence.entity.Versions


//  Created by Rodrigo G. Resende on 2019-12-25.

class RemoteConfig(private val remoteConfig: FirebaseRemoteConfig){

    fun initRemoteConfig(){
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_defaults)
        remoteConfig.fetch()
            .addOnSuccessListener {
                remoteConfig.activate()
            }
    }

    fun getVersions(): List<Version> {
        val versions = remoteConfig.getString("sigaa_updates")
        return try {
            Gson().fromJson(versions, Versions::class.java).versions
        }
        catch(e: Exception){
            listOf()
        }
    }

    fun isNotificationsEnabled(): Boolean {
        return remoteConfig.getBoolean("notifications_enabled")
    }


}