package com.rodrigmatrix.sigaaufc.data

import android.util.Log
import com.rodrigmatrix.sigaaufc.data.network.ConnectivityInterceptor
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.JavaxFaces
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.serializer.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.SocketTimeoutException
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.CookieJar






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
                .build()
            try {
                val response = httpClient
                    .addInterceptor(connectivityInterceptor)
                    .build()
                    .newCall(request)
                    .execute()
                if(response.isSuccessful){
                    val res = response.body?.string()
                    status = true
                    val arr = response.request.url.toString().split("jsessionid=")
                    val student = studentDatabase.studentDao().getStudentAsync()
                    if(student == null){
                        studentDatabase.studentDao().upsertStudent(Student(
                            arr[1], "", "", "", "", "", false, "", ""))
                    }
                    else{
                        student.jsession = arr[1]
                        studentDatabase.studentDao().upsertStudent(student)
                        status = true
                    }
                }
                else{
                    status = false
                }
            }
            catch (e: SocketTimeoutException) {
                status = false
                Log.e("Connectivity", "No internet Connection.", e)
            }
        }
        return status
    }

    private fun saveViewState(res: String?){
        val viewStateString = res!!.split("id=\"javax.faces.ViewState\" value=\"")
        val viewStateId = viewStateString[1].split("\" ")[0]
        val viewState = studentDatabase.studentDao().getViewStateAsync()
        println("viewstate to save $viewState")
        studentDatabase.studentDao().upsertViewState(JavaxFaces(true, viewStateId))
    }

    private suspend fun getViewStateAsync(): JavaxFaces{
        return withContext(Dispatchers.IO){
            return@withContext studentDatabase.studentDao().getViewStateAsync()
        }
    }

    suspend fun login(cookie: String, login: String, password: String): String{
        println("cookie login $cookie")
        return withContext(Dispatchers.IO){
            var status = ""
            val formBody = FormBody.Builder()
                .add("width", "0")
                .add("height", "0")
                .add("user.login", login)
                .add("user.senha", password)
                .add("entrar", "Entrar")
                .add("urlRedirect", "")
                .add("acao","")
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/logar.do?dispatch=logOn")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/verTelaLogin.do")
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
                    val res = response.body?.string()
                    println(res)
                    status = "Erro de conexão"
                }
            }
            catch(e: NoConnectivityException){
                status = "Sem conexão com a internet"
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                println("catch expirou")
                status = "Tempo de conexão expirou"
                Log.e("Connectivity", "No internet Connection.", e)
            }
            return@withContext status
        }
    }
    private suspend fun redirectMenu(cookie: String): String{
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/paginaInicial.do")
            .header("Cookie", "JSESSIONID=$cookie")
            .header("Referer", "https://si3.ufc.br/sigaa/verTelaLogin.do%3bjsessionid=$cookie")
            .build()
        return withContext(Dispatchers.IO){
            var status = ""
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
            else{
                status = "Tempo de conexão expirou"
            }
            return@withContext status
        }
    }
    private suspend fun getClasses(cookie: String): String{
        val request = Request.Builder()
            .url("https://si3.ufc.br/sigaa/verPortalDiscente.do")
            .header("Cookie", "JSESSIONID=$cookie")
            .header("Referer", "https://si3.ufc.br/sigaa/pag-inaInicial.do")
            .header("Host", "si3.ufc.br")
            .build()
        return withContext(Dispatchers.IO){
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            var status = ""
            if(response.isSuccessful){
                val res = response.body?.string()
                saveViewState(res)
                val pair = sigaaSerializer.parseClasses(res)
                studentDatabase.studentDao().deleteClasses()
                pair.second.forEach {
                    studentDatabase.studentDao().insertClass(it)
                }
                val student = studentDatabase.studentDao().getStudentAsync()
                if(student != null){
                    val pairStudent = pair.first
                    student.course = pairStudent.course
                    student.matricula = pairStudent.matricula
                    student.name = pairStudent.name
                    student.profilePic = pairStudent.profilePic
                    studentDatabase.studentDao().upsertStudent(student)
                }
                status = "Success"
            }
            else{
                val error = response.body?.string()
//                println(error)
                status = when {
                    error!!.contains("Usuário Não Autorizado") -> "Acesso negado. Tente efetuar login novamente"
                    error.contains("O sistema comportou-se de forma inesperada") -> "Erro ao carregar disciplinas. Tente efetuar login novamente"
                    else -> "Erro ao efetuar login. Tente novamente"
                }
            }
            return@withContext status
        }
    }

    suspend fun getPreviousClasses(cookie: String): Pair<String, MutableList<StudentClass>>{
        return withContext(Dispatchers.IO){
            var status = ""
            var listClasses = mutableListOf<StudentClass>()
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
                status = "Erro ao efetuar login. Tente novamente"
            }
            return@withContext Pair(status, listClasses)
        }
    }

    suspend fun getClass(id: String, idTurma: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        println(viewState)
        println("cookie getclass $cookie")
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
                .add("form_acessarTurmaVirtual$id", "form_acessarTurmaVirtual$id")
                .add("idTurma", idTurma)
                .add("javax.faces.ViewState", viewState)
                .add("form_acessarTurmaVirtual$id:turmaVirtual$id", "form_acessarTurmaVirtual$id:turmaVirtual$id")
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
                saveViewState(res)
                getGrades(idTurma, cookie)
                //println(res)
            }
            else{
                val res = response.body?.string()
                //println(res)
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

    suspend fun getGrades(idTurma: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            var status = "Tempo de conexão expirou"
            val formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add("formMenu:j_id_jsp_1287906063_20", "formMenu:j_id_jsp_1287906063_20")
                .add("formMenu:j_id_jsp_1287906063_3", "formMenu:j_id_jsp_1287906063_18")
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br//sigaa/ava/index.jsf")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
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
                    println(res)
                }
            }
            catch(e: NoConnectivityException){
                status = "Sem conexão com a internet"
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                status = "Tempo de conexão expirou"
                Log.e("Connectivity", "No internet Connection.", e)
            }
            val response = httpClient
                .addInterceptor(connectivityInterceptor)
                .build()
                .newCall(request)
                .execute()
            if(response.isSuccessful){
                val res = response.body?.string()
                sigaaSerializer.parseGrades(idTurma, res)
            }
        }
    }

    suspend fun getRU(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>>{
        val formBody = FormBody.Builder()
            .add("codigoCartao", numeroCartao)
            .add("matriculaAtreladaCartao", matricula)
            .build()
        val history = mutableListOf<HistoryRU>()
        var status = ""
        var name = ""
        var credits = 0
        return withContext(Dispatchers.IO) {
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
            catch (e: SocketTimeoutException) {
                status = "Tempo de conexão expirou"
                Log.e("Connectivity", "No internet Connection.", e)
            }
            return@withContext Triple(status, history, Pair(name, credits))
        }
    }
}