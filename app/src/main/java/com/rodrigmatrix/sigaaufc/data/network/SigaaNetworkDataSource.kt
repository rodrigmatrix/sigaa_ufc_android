package com.rodrigmatrix.sigaaufc.data.network

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.Student

interface SigaaNetworkDataSource {


    suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    ): String

    suspend fun getCookie(): Boolean

    suspend fun fetchRu(numeroCartao: String, matricula: String): Pair<String, MutableList<HistoryRU>>
}