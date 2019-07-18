package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.Student


interface SigaaRepository {
    suspend fun login(
        cookie: String,
        login: String,
        password: String
    ): String

    suspend fun getStudent(): LiveData<out Student>

    suspend fun saveLogin(login: String, password: String)

    suspend fun getCookie(): Boolean
}