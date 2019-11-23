package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU

class SigaaNetworkDataSourceImpl(
    private val sigaaApi: SigaaApi
) : SigaaNetworkDataSource {

    override suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    ): String {
        return sigaaApi.login(cookie, login, password)
    }

    override suspend fun getCookie(): Boolean {
        return sigaaApi.getCookie()
    }

    override suspend fun fetchRu(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>> {
        return sigaaApi.getRU(numeroCartao, matricula)
    }

    override suspend fun fetchGrades(viewStateId: String, cookie: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun fetchClass(id: String, idTurma: String, cookie: String) {
        return sigaaApi.getClass(id, idTurma, cookie)
    }

    override suspend fun fetchPreviousClass(id: String, idTurma: String, cookie: String) {
        return sigaaApi.getPreviousClass(id, idTurma, cookie)
    }

    override suspend fun fetchCurrentClasses(cookie: String): String {
        return sigaaApi.getClasses(cookie)
    }

    override suspend fun fetchPreviousClasses(cookie: String) {
        return sigaaApi.getPreviousClasses(cookie)
    }

    override suspend fun fetchNews(
        cookie: String,
        newsId: String,
        requestId: String,
        requestId2: String) {
        return sigaaApi.fetchNewsContent(cookie, newsId, requestId, requestId2)
    }

    override suspend fun fetchNewsPage(idTurma: String, requestId: String, cookie: String) {
        return sigaaApi.getNews(idTurma, requestId, cookie)
    }

    override suspend fun setVinculo(cookie: String, vinculo: String) {
        return sigaaApi.setVinculo(cookie, vinculo)
    }

    override suspend fun getHistorico(id: String, cookie: String) {
        return sigaaApi.getHistorico(id, cookie)
    }

}