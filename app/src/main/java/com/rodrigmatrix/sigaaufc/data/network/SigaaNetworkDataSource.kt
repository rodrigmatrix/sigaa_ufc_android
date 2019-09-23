package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.Vinculo

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

    suspend fun fetchCurrentClasses(cookie: String): String

    suspend fun fetchNews(cookie: String, newsId: String, requestId: String, requestId2: String)

    suspend fun fetchNewsPage(idTurma: String, requestId: String, cookie: String)

    suspend fun setVinculo(cookie: String, vinculo: String)

    suspend fun getHistorico(id: String, cookie: String)

}