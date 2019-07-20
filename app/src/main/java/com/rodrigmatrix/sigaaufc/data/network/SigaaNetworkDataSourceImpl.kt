package com.rodrigmatrix.sigaaufc.data.network

import android.util.Log
import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU

class SigaaNetworkDataSourceImpl(
    private val sigaaApi: SigaaApi
) : SigaaNetworkDataSource {

    override suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    ): String {
        var res: String
        try {
            res = sigaaApi.login(cookie, login, password)
        }
        catch (e: NoConnectivityException){
            res = "Sem conexão com a internet"
            Log.e("Connectivity", "No internet Connection.", e)
        }
        return res
    }

    override suspend fun getCookie(): Boolean {
        return sigaaApi.getCookie()
    }

    override suspend fun fetchRu(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>> {
        return sigaaApi.getRU(numeroCartao, matricula)
    }
}