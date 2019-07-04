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
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
        var cookie = ""
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa")
                .header("Referer", "https://si3.ufc.br")
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
        var status = ""
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("width", "0")
                .add("height", "0")
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .add("javax.faces.ViewState", "j_id3")
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
                when(serializer.loginParse(res)){
                    "Continuar" -> {
                        redirectMenu(cookie)
                        status = "Success"
                    }
                    "Menu Principal" -> {
                        status = "Success"
                        redirectMenu(cookie)
                    }
                    "Usuário e/ou senha inválidos" -> {
                        status = "Usuário ou senha inválidos"
                    }
                    else -> {
                        status = "Erro ao efetuar login"
                    }
                }
            }
            else{
                status = "Erro De Conexão"
            }
        }
        return status
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
                }
            }
        }
    }
    private suspend fun getClasses(cookie: String): String?{
        val client = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
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
                serializer.parseClasses(res)
                getClass(" ", " ",cookie)
            }
        }
        return classes
    }

    private suspend fun getClass(idTurma: String, id: String, cookie: String){
        val client = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("idTurma", "378807")
                .add("form_acessarTurmaVirtualj_id_1", "form_acessarTurmaVirtualj_id_1")
                .add("form_acessarTurmaVirtualj_id_1:turmaVirtualj_id_1", "form_acessarTurmaVirtualj_id_1:turmaVirtualj_id_1")
                .add("javax.faces.ViewState", "j_id2")
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/discente.jsf#")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .post(formBody)
                .build()
            var response = client.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                getGrades(" ", " ", cookie)
            }
        }
    }

    private suspend fun getGrades(idTurma: String, id: String, cookie: String){
        val client = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add("formMenu:j_id_jsp_1287906063_20", "formMenu:j_id_jsp_1287906063_20")
                .add("formMenu:j_id_jsp_1287906063_3", "formMenu:j_id_jsp_1287906063_18")
                .add("javax.faces.ViewState", "j_id3")
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br//sigaa/ava/index.jsf")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .post(formBody)
                .build()
            var response = client.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                println(res)
            }
        }
    }

    suspend fun getRU(numeroCartao: String, matricula: String): Triple<String, Pair<String, Int>, MutableList<HistoryRU>>{
        var triple = Triple("Tempo de conexão expirou", Pair("", 1), mutableListOf<HistoryRU>())
        var formBody = FormBody.Builder()
            .add("codigoCartao", numeroCartao)
            .add("matriculaAtreladaCartao", matricula)
            .build()
        val client = OkHttpClient().newBuilder()
            .connectTimeout(40, TimeUnit.SECONDS)
            .writeTimeout(40, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
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