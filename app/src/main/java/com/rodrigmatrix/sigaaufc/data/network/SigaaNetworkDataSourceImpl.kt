package com.rodrigmatrix.sigaaufc.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException

class SigaaNetworkDataSourceImpl(
    private val sigaaApi: SigaaApi
) : SigaaNetworkDataSource {

    private val _downloadedLogin = MutableLiveData<String>()
    override val downloadedLogin: LiveData<String>
        get() = _downloadedLogin

    override suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String,
        comando: String
    ) {
        try {
            val fetchedLogin = sigaaApi
                .login(cookie, login, password, comando)
            _downloadedLogin.postValue(fetchedLogin)
        }
        catch (e: NoConnectivityException){
            Log.e("Connectivity", "No internet Connection.", e)
        }
    }
}