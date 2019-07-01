package com.rodrigmatrix.sigaaufc.api

import com.rodrigmatrix.sigaaufc.persistence.HistoryRU
import com.rodrigmatrix.sigaaufc.serializer.Serializer
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
            }
        }
        return cookie
    }

    suspend fun login(cookie: String, login: String, password: String): String{
        val serializer  = Serializer()
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        var res = ""
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
            if(response.isSuccessful){
                val res = response.body?.string()
                //println(response.body?.string())
                when(serializer.loginParse(res)){
                    "Continuar" -> redirectMenu(cookie)

                }
                //var arr = response.request.url.toString().split("jsessionid=")
                //println("cookie: " + arr[1])
            }
            else{
                res = "Erro De Conexão"
            }
        }
        return res
    }
    private suspend fun redirectMenu(cookie: String){
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/paginaInicial.do")
            .header("Cookie", "JSESSIONID=$cookie")
            .build()
        withContext(Dispatchers.IO){
            var response = client.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                if(res!!.contains("Menu Principal")){
                    var classes = getClasses(cookie)
                    //println(classes)
                }
            }
            else{
                redirectMenu(cookie)
            }
        }
    }
    private suspend fun getClasses(cookie: String): String?{
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        var classes = ""
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/verPortalDiscente.do")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/pag-inaInicial.do")
                .build()
            var response = client.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                val serializer = Serializer()
                classes = res.toString()
                serializer.parseClasses(res)
            }
        }
        return classes
    }

    suspend fun getRU(matricula: String, numeroCartao: String): Triple<String, Pair<String, Int>, MutableList<HistoryRU>>{
        var triple = Triple("Tempo de conexão expirou", Pair("", 1), mutableListOf<HistoryRU>())
        var formBody = FormBody.Builder()
            .add("codigoCartao", numeroCartao)
            .add("matriculaAtreladaCartao", matricula)
            .build()
        val client = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/public/restauranteConsultarSaldo.do")
                .post(formBody)
                .build()
            val response = client.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                val serializer = Serializer()
                triple = serializer.parseRU(res)
            }
        }
        return triple
    }
}