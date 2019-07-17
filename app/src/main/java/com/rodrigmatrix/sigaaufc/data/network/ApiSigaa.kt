package com.rodrigmatrix.sigaaufc.data.network

import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


class ApiSigaa(
    private val httpClient: OkHttpClient,
    private val sigaaSerializer: Serializer
) {

    suspend fun getCookie(): Pair<Boolean, String>{
        var cookie = ""
        var status = false
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa")
                .header("Referer", "https://si3.ufc.br")
                .build()
            var response = httpClient.newCall(request).execute()
            if(response != null && response.isSuccessful){
                status = true
                var arr = response.request().url().toString().split("jsessionid=")
                cookie = arr[1]
            }
            else{
                status = false
            }
        }
        return Pair(status, cookie)
    }

    suspend fun login(cookie: String, login: String, password: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        var listClass = mutableListOf<StudentClass>()
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
                .header("Referer", "https://si3.ufc.br/sigaa/verTelaLogin.do")
                .post(formBody)
                .build()
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                println(res)
                when(sigaaSerializer.loginParse(res)){
                    "Continuar" -> {
                        var res = redirectMenu(cookie)
                        if(res.first != "Success"){
                            status = res.first
                        }
                        else{
                            status = "Success"
                            listClass = res.second
                        }

                    }
                    "Menu Principal" -> {
                        var res = redirectMenu(cookie)
                        if(res.first != "Success"){
                            status = res.first
                        }
                        else{
                            status = "Success"
                            listClass = res.second
                        }
                    }
                    "Usuário e/ou senha inválidos" -> {
                        status = "Usuário ou senha inválidos"
                    }
                    else -> {
                        status = sigaaSerializer.loginParse(res)
                    }
                }
            }
            else{
                status = "Erro de conexão"
            }
        }
        return Pair(status, listClass)
    }
    private suspend fun redirectMenu(cookie: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        var listClass = mutableListOf<StudentClass>()
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/paginaInicial.do")
            .header("Cookie", "JSESSIONID=$cookie")
            .build()
        withContext(Dispatchers.IO){
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                println("redirect")
                val res = response.body()?.string()
                if(res!!.contains("Menu Principal")){
                    var pair = getClasses(cookie)
                    if(pair.first != "Success"){
                        status = "Tempo de conexão expirou"
                    }
                    else{
                        status = "Success"
                        listClass = pair.second
                    }
                }
            }
        }
        return Pair(status, listClass)
    }
    private suspend fun getClasses(cookie: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        var listClasses = mutableListOf<StudentClass>()
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/verPortalDiscente.do")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/pag-inaInicial.do")
                .build()
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                listClasses = sigaaSerializer.parseClasses(res)
                var viewStateId = res!!.split("id=\"javax.faces.ViewState\" value=\"")
                viewStateId = viewStateId[1].split("\" ")
                status = "Success"
            }
            else{
                status = "Tempo de conexão expirou"
            }
        }
        return Pair(status, listClasses)
    }

    suspend fun getPreviousClasses(cookie: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        var listClasses = mutableListOf<StudentClass>()
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/turmas.jsf")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .build()
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                listClasses = sigaaSerializer.parsePreviousClasses(res)
                status = "Success"
            }
            else{
                status = "Tempo de conexão expirou"
            }
        }
        return Pair(status, listClasses)
    }

    suspend fun getClass(viewStateId: String, idTurma: String, id: Int, cookie: String){
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("idTurma", idTurma)
                .add("form_acessarTurmaVirtualj_id_$id", "form_acessarTurmaVirtualj_id_$id")
                .add("form_acessarTurmaVirtualj_id_$id:turmaVirtualj_id_$id", "form_acessarTurmaVirtualj_id_$id:turmaVirtualj_id_$id")
                .add("javax.faces.ViewState", viewStateId)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/discente.jsf#")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .post(formBody)
                .build()
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                //println(res)
                var viewStateId = res!!.split("id=\"javax.faces.ViewState\" value=\"")
                viewStateId = viewStateId[1].split("\" ")
                getGrades(viewStateId[0], cookie)
            }
        }
    }

    suspend fun getPreviousClass(cookie: String, idTurma: String, id: String, viewStateId: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        var list = mutableListOf<StudentClass>()
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("idTurma", idTurma)
                .add("j_id_jsp_1344809141_$id", "j_id_jsp_1344809141_$id")
                .add("j_id_jsp_1344809141_2:j_id_jsp_1344809141_4", "j_id_jsp_1344809141_2:j_id_jsp_1344809141_4")
                .add("javax.faces.ViewState", viewStateId)
                .add("idTurma", idTurma)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/turmas.jsf")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .post(formBody)
                .build()
            var response = httpClient.newCall(request).execute()
            status = when {
                response.isSuccessful -> {
                    val res = response.body()?.string()
                    sigaaSerializer.parseNews(res)
                    "Success"
                }
                else -> "Tempo de conexão expirou"
            }
        }
        return Pair(status, list)
    }

    private suspend fun getGrades(viewStateId: String, cookie: String){
        withContext(Dispatchers.IO){
            var formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add("formMenu:j_id_jsp_1287906063_20", "formMenu:j_id_jsp_1287906063_20")
                .add("formMenu:j_id_jsp_1287906063_3", "formMenu:j_id_jsp_1287906063_18")
                .add("javax.faces.ViewState", viewStateId)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br//sigaa/ava/index.jsf")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .post(formBody)
                .build()
            var response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                //println(res)
            }
        }
    }

    suspend fun getRU(numeroCartao: String, matricula: String): Triple<String, Pair<String, Int>, MutableList<HistoryRU>>{
        var triple = Triple("Tempo de conexão expirou", Pair("", 1), mutableListOf<HistoryRU>())
        var formBody = FormBody.Builder()
            .add("codigoCartao", numeroCartao)
            .add("matriculaAtreladaCartao", matricula)
            .build()
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/public/restauranteConsultarSaldo.do")
                .post(formBody)
                .build()
            val response = httpClient.newCall(request).execute()
            if(response.isSuccessful){
                val res = response.body()?.string()
                triple = sigaaSerializer.parseRU(res)
            }
        }
        return triple
    }
}