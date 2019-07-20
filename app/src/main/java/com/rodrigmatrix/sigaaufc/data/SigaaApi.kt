package com.rodrigmatrix.sigaaufc.data

import android.util.Log
import androidx.lifecycle.Observer
import com.rodrigmatrix.sigaaufc.data.network.ConnectivityInterceptor
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException
import com.rodrigmatrix.sigaaufc.internal.TimeoutException
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request


class SigaaApi(
    private val httpClient: OkHttpClient.Builder,
    private val sigaaSerializer: Serializer,
    private val studentDatabase: StudentDatabase,
    private val connectivityInterceptor: ConnectivityInterceptor
) {

    suspend fun getCookie(): Boolean{
        var status = false
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa")
                .header("Referer", "https://si3.ufc.br")
                .build()
            try {
                val response = httpClient
                    .addInterceptor(connectivityInterceptor)
                    .build()
                    .newCall(request)
                    .execute()
                if(response.isSuccessful){
                    status = true
                    val arr = response.request.body.toString().split("jsessionid=")
                    val student = studentDatabase.studentDao().getStudent()
                    if(student == null){
                        studentDatabase.studentDao().upsertStudent(Student(
                            arr[1], "", "", "", "", "", "", "", false,
                            ""))
                    }
                    else{
                        student.jsession = arr[1]
                        studentDatabase.studentDao().upsertStudent(student)
                    }
                }
                else{
                    status = false
                }
            }
            catch(e: TimeoutException){
                Log.d("Timeout Exception ",e.toString())
            }
        }
        return status
    }

    suspend fun login(cookie: String, login: String, password: String): String{
        var status = ""
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
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                val parser = sigaaSerializer.loginParse(res)
                status = when(parser){
                    "Continuar" -> {
                        redirectMenu(cookie)
                    }
                    "Menu Principal" -> {
                        redirectMenu(cookie)
                    }
                    "Usuário e/ou senha inválidos" -> {
                        "Usuário ou senha inválidos"
                    }
                    else -> {
                        parser
                    }
                }
            }
            else{
                status = "Erro de conexão"
            }
        }
        return status
    }
    private suspend fun redirectMenu(cookie: String): String{
        var status = "Tempo de conexão expirou"
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/paginaInicial.do")
            .header("Cookie", "JSESSIONID=$cookie")
            .build()
        withContext(Dispatchers.IO){
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                if(res!!.contains("Menu Principal")){
                    val res = getClasses(cookie)
                    status = if(res != "Success"){
                        "Tempo de conexão expirou"
                    } else{
                        "Success"
                    }
                }
            }
        }
        return status
    }
    private suspend fun getClasses(cookie: String): String{
        var status = ""
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/verPortalDiscente.do")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/pag-inaInicial.do")
                .build()
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                val listClasses = sigaaSerializer.parseClasses(res)
                studentDatabase.studentDao().deleteClasses()
                listClasses.forEach {
                    studentDatabase.studentDao().insertClass(it)
                }
                var viewStateId = res!!.split("id=\"javax.faces.ViewState\" value=\"")
                viewStateId = viewStateId[1].split("\" ")
                status = "Success"
            }
            else{
                status = "Tempo de conexão expirou"
            }
        }
        return status
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
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
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
            val formBody = FormBody.Builder()
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
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                //println(res)
                var viewStateId = res!!.split("id=\"javax.faces.ViewState\" value=\"")
                viewStateId = viewStateId[1].split("\" ")
                getGrades(viewStateId[0], cookie)
            }
        }
    }

    suspend fun getPreviousClass(cookie: String, idTurma: String, id: String, viewStateId: String): Pair<String, MutableList<StudentClass>>{
        var status = ""
        val list = mutableListOf<StudentClass>()
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
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
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            status = when {
                response.isSuccessful -> {
                    val res = response.body?.string()
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
            val formBody = FormBody.Builder()
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
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                //println(res)
            }
        }
    }

    suspend fun getRU(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>>{
        val formBody = FormBody.Builder()
            .add("codigoCartao", numeroCartao)
            .add("matriculaAtreladaCartao", matricula)
            .build()
        val history = mutableListOf<HistoryRU>()
        var status = "Tempo de conexão expirou"
        var name = ""
        var credits = 0
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://si3.ufc.br/public/restauranteConsultarSaldo.do")
                .post(formBody)
                .build()
            try {
                val response = httpClient
                    .addInterceptor(connectivityInterceptor)
                    .build()
                    .newCall(request)
                    .execute()
                if(response.isSuccessful){
                    val res = response.body?.string()
                    val triple = sigaaSerializer.parseRU(res)
                    status = triple.first
                    if(triple.first == "Success"){
                        name = triple.second.first
                        credits = triple.second.second
                        triple.third.forEach {
                            history.add(it)
                        }
                    }
                    else{
                        status = triple.first
                    }
                }
                else{
                    status = "Erro de conexão"
                }
            }
            catch(e: NoConnectivityException){
                status = "Sem conexão com a internet"
                Log.e("Connectivity", "No internet Connection.", e)
            }

        }
        return Triple(status, history, Pair(name, credits))
    }
}