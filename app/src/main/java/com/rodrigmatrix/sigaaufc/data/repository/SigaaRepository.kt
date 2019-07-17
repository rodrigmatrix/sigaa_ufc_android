package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.Student


interface SigaaRepository {
    suspend fun login(
        cookie: String,
        login: String,
        password: String,
        comando: String
    ): LiveData<out Student>
}