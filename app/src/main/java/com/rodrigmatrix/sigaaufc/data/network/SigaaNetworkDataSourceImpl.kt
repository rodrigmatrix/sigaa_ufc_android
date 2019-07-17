package com.rodrigmatrix.sigaaufc.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rodrigmatrix.sigaaufc.data.SigaaApi
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException
import com.rodrigmatrix.sigaaufc.internal.TimeoutException
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SigaaNetworkDataSourceImpl(
    private val sigaaApi: ApiSigaa,
    private val sigaaSerializer: Serializer
) : SigaaNetworkDataSource {

    private val _downloadedLogin = MutableLiveData<Student>()
    override val downloadedLogin: LiveData<Student>
        get() = _downloadedLogin

    override suspend fun fetchLogin(
        cookie: String,
        login: String,
        password: String
    ) {
        try {
            val fetchedLogin = sigaaApi
                .login(cookie, login, password)
        }
        catch (e: NoConnectivityException){
            Log.e("Connectivity", "No internet Connection.", e)
        }
    }

    private fun loginHandler(res: String): String{
        return when(res){
            "Continuar" -> "Success"
            "Menu Principal" -> "Success"
            "Usuário e/ou senha inválidos" -> "Usuário ou senha não encontrados"
            else -> "Erro ao efetuar login"
        }
    }
}