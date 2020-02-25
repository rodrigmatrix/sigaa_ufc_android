package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.internal.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import retrofit2.HttpException
import java.lang.Exception

class SigaaDataSource(
    private val sigaaApi: SigaaApi
) {

    suspend fun login(
        login: String,
        password: String
    ): Result<String> = withContext(Dispatchers.IO){
        return@withContext try {
            val formBody = FormBody.Builder()
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .build()
            val request = sigaaApi.login(formBody)
            Result.Success(request.string())
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