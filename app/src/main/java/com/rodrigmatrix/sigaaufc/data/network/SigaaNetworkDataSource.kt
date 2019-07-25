package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU

interface SigaaNetworkDataSource {


    suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    ): String

    suspend fun getCookie(): Boolean

    suspend fun fetchGrades(viewStateId: String, cookie: String): String

    suspend fun fetchRu(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>>

    suspend fun fetchClass(id: String, idTurma: String, cookie: String)
}