package com.rodrigmatrix.sigaaufc.data.network

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.Class

interface SigaaNetworkDataSource {

    val downloadedLogin: LiveData<String>

    suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String,
        comando: String
    )
}