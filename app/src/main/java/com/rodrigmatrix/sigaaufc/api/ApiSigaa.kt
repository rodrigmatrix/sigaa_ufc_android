package com.rodrigmatrix.sigaaufc.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


class ApiSigaa {


    suspend fun getCookie(): String{
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        var cookie = ""
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa")
                .header("referer", "https://si3.ufc.br")
                .build()
            var response = client.newCall(request).execute()
            if(response != null && response.isSuccessful){
                var arr = response.request.url.toString().split("jsessionid=")
                cookie = arr[1]
            }
            else{
                //error
            }
        }
        return cookie
    }

    suspend fun login(cookie: String, login: String, password: String){
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("width", "0")
                .add("height", "0")
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/logar.do?dispatch=logOn")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/verTelaLogin.do%3bjsessionid=$cookie")
                .post(formBody)
                .build()
            var response = client.newCall(request).execute()
            if(response != null && response.isSuccessful){
                println(response)
                //var arr = response.request.url.toString().split("jsessionid=")
                //println("cookie: " + arr[1])
            }
            else{
                //error
            }
        }
    }
}