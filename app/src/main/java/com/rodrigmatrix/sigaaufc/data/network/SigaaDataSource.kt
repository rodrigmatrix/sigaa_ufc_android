package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.internal.LoginException
import com.rodrigmatrix.sigaaufc.internal.Result
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_ERROR
import com.rodrigmatrix.sigaaufc.persistence.entity.LoginStatus.Companion.LOGIN_SUCCESS
import com.rodrigmatrix.sigaaufc.serializer.NewSerializer
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import retrofit2.HttpException
import java.lang.Exception

class SigaaDataSource(
    private val sigaaApi: SigaaApi
) {

    private val serializer = NewSerializer()

    suspend fun login(
        login: String,
        password: String
    ): Result<LoginStatus> = withContext(Dispatchers.IO){
        return@withContext try {
            val formBody = FormBody.Builder()
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .build()
            val request = sigaaApi.login(formBody)
            val loginResponse = serializer.parseLogin(request.string())
            when(loginResponse.loginStatus){
                LOGIN_SUCCESS -> Result.Success(loginResponse)
                else -> Result.Error(LoginException(loginResponse.loginMessage))
            }
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }

    suspend fun setVinculo(vinculo: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val map = hashMapOf<String, String>()
            map["dispatch"] = "escolher"
            map["vinculo"] = vinculo
            val request = sigaaApi.setVinculo(map)
            Result.Success(request.string())
        }
        catch(e: HttpException){
            Result.Error(e)
        }
        catch(e: Exception){
            Result.Error(e)
        }
    }


}