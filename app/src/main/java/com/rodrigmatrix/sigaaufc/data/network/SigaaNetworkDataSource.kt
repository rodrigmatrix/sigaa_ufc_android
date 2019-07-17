package com.rodrigmatrix.sigaaufc.data.network

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.Student

interface SigaaNetworkDataSource {

    val downloadedLogin: LiveData<Student>

    suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    )
}