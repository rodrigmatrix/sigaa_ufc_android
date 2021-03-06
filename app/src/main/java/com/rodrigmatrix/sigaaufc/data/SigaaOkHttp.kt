package com.rodrigmatrix.sigaaufc.data

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import com.rodrigmatrix.sigaaufc.data.network.ConnectivityInterceptor
import com.rodrigmatrix.sigaaufc.internal.NoConnectivityException
import com.rodrigmatrix.sigaaufc.internal.TimeoutException
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


@Suppress("DEPRECATION")
class SigaaOkHttp(
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
                    val cookie = response.request.url.toString()
                    status = true
                    val arr = cookie.split("jsessionid=")
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
            catch(e: NoConnectivityException){
                status = false
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                status = false
                Log.e("Connectivity", "Timeout Exception", e)
            }
        }
        return status
    }

    private fun saveViewState(res: String?){
        try {
            val viewStateString = res!!.split("id=\"javax.faces.ViewState\" value=\"")
            val viewStateId = viewStateString[1].split("\" ")[0]
            val viewState = studentDatabase.studentDao().getViewStateAsync()
            println("viewstate to save $viewState")
            studentDatabase.studentDao().upsertViewState(JavaxFaces(true, viewStateId))
        }catch(e: IndexOutOfBoundsException){
            println("SAVEVIEWSTATE $e")
        }
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
                    val parser = sigaaSerializer.parseLogin(res)
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
                        "Vinculo" -> {
                            studentDatabase.studentDao().deleteVinculos()
                            sigaaSerializer.getVinculoId(res).forEach {
                                studentDatabase.studentDao().upsertVinculos(it)
                            }
                            "Vinculo"
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



    suspend fun setVinculo(cookie: String, vinculoId: String){
        withContext(Dispatchers.IO){
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/escolhaVinculo.do?dispatch=escolher&vinculo=$vinculoId")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/vinculos.jsf")
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
                    getClasses(cookie)
                } else{
                    val res = response.body?.string()
                    println(res)
                }
            }
            catch(e: NoConnectivityException){
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                println("catch expirou")
                Log.e("Connectivity", "No internet Connection.", e)
            }
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
    suspend fun getClasses(cookie: String): String{
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
                var newClasses = mutableListOf<StudentClass>()
                try {
                    pair.second.forEach {
                        it.synced = studentDatabase.studentDao().getClassWithIdTurmaAsync(it.turmaId).isPrevious
                        newClasses.add(it)
                    }
                }
                catch(e: Exception){
                    newClasses = pair.second
                }
                studentDatabase.studentDao().deleteClasses()
                newClasses.forEach {
                    studentDatabase.studentDao().upsertClass(it)
                }
                val student = studentDatabase.studentDao().getStudentAsync()
                if(student != null){
                    val pairStudent = pair.first
                    student.course = pairStudent.course
                    student.matricula = pairStudent.matricula
                    student.name = pairStudent.name
                    student.profilePic = pairStudent.profilePic
                    student.nivel = pairStudent.nivel
                    student.entrada = pairStudent.entrada
                    student.email = pairStudent.email
                    student.lastUpdate = sigaaSerializer.parseIraRequestId(res).first
                    studentDatabase.studentDao().upsertStudent(student)
                }
                status = "Success"
                val pairIra = sigaaSerializer.parseIraRequestId(res)
                getIra(pairIra.first, pairIra.second, cookie)
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

    suspend fun getPreviousClasses(cookie: String) {
        withContext(Dispatchers.IO){
            var listClasses = mutableListOf<StudentClass>()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/turmas.jsf")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
                .build()
            try{
                val response = httpClient
                    .addInterceptor(connectivityInterceptor)
                    .build()
                    .newCall(request)
                    .execute()
                if(response.isSuccessful){
                    val res = response.body?.string()
                    saveViewState(res)
                    listClasses = sigaaSerializer.parsePreviousClasses(res)
                    listClasses.forEach {
                        studentDatabase.studentDao().upsertClass(it)
                    }
                }
            }catch(e: TimeoutException){
            }

        }
    }

    private suspend fun getIra(id: String, script: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            var status = ""
            var listClasses = mutableListOf<StudentClass>()
            val formBody = FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", id)
                .add("jscook_action", script)
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
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
                    studentDatabase.studentDao().deleteIra()
                    studentDatabase.studentDao().upsertIra(sigaaSerializer.parseIra(res))
                }
                else{
                    status = ""
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
        }
    }

    private suspend fun getAtestado(id: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", id)
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_440181972_4_menu:A]#{ portalDiscente.atestadoMatricula }")
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
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
                    saveViewState(res)
                    println(res)
                }
                else{
                }
            }
            catch(e: NoConnectivityException){
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                Log.e("Connectivity", "No internet Connection.", e)
            }
        }
    }

    suspend fun getHistorico(id: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
                .add("menu:form_menu_discente", "menu:form_menu_discente")
                .add("id", id)
                .add("jscook_action", "menu_form_menu_discente_j_id_jsp_440181972_4_menu:A]#{ portalDiscente.historico }")
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/portais/discente/discente.jsf")
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
                    val content = response.body?.byteStream()!!.readBytes()
                    val directory = Environment.getExternalStorageDirectory().toString() + "/Download"
                    val dir = java.io.File(directory)
                    if(!dir.exists()){
                        dir.mkdirs()
                    }
                    val file = java.io.File("$directory/historico_sigaa.pdf")
                    file.createNewFile()
                    java.io.File(directory, "historico_sigaa.pdf").writeBytes(content)
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    builder.detectFileUriExposure()
                    val openFile = java.io.File("$directory/historico_sigaa.pdf")
                    val target = Intent(Intent.ACTION_VIEW)
                    target.setDataAndType((Uri.fromFile(openFile)), "application/pdf")
                    target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                }
                else{
                }
            }
            catch(e: NoConnectivityException){
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                Log.e("Connectivity", "No internet Connection.", e)
            }
        }
    }



    suspend fun getClass(id: String, idTurma: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            studentDatabase.studentDao().deleteFiles(idTurma)
            val formBody = FormBody.Builder()
                .add("idTurma", idTurma)
                .add("form_acessarTurmaVirtual$id", "form_acessarTurmaVirtual$id")
                .add("form_acessarTurmaVirtual$id:turmaVirtual$id", "form_acessarTurmaVirtual$id:turmaVirtual$id")
                .add("javax.faces.ViewState", viewState)
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
                val files = sigaaSerializer.parseFiles(res, idTurma)
                files.forEach {
                    studentDatabase.studentDao().upsertFile(it)
                }
                val newsRequestId = sigaaSerializer.parseNewsRequestId(res)
                val studentClass = studentDatabase.studentDao().getClassWithIdTurmaAsync(idTurma)
                studentClass.code = newsRequestId
                studentDatabase.studentDao().upsertClass(studentClass)
                getGrades(
                    sigaaSerializer.parseGradesRequestId(res),
                    idTurma,
                    cookie
                )
                getAttendance(
                    false,
                    sigaaSerializer.parseAttendanceRequestId(res),
                    idTurma,
                    cookie
                )

                getNews(
                    idTurma,
                    newsRequestId,
                    cookie
                )
            }
            else{
                val res = response.body?.string()
                //println(res)
            }
        }
    }

    suspend fun getPreviousClass(id: String, idTurma: String, cookie: String){
        var status = ""
        val list = mutableListOf<StudentClass>()
        val viewStateId = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
                .add("j_id_jsp_1344809141_2", "j_id_jsp_1344809141_2")
                .add("j_id_jsp_1344809141_2:j_id_jsp_1344809141_4$id", "j_id_jsp_1344809141_2:j_id_jsp_1344809141_4$id")
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
                    saveViewState(res)
                    val files = sigaaSerializer.parseFiles(res, idTurma)
                    files.forEach {
                        studentDatabase.studentDao().upsertFile(it)
                    }
                    val newsRequestId = sigaaSerializer.parseNewsRequestId(res)
                    val studentClass = studentDatabase.studentDao().getPreviousClassWithIdTurmaAsync(idTurma)
                    studentClass.code = newsRequestId
                    studentDatabase.studentDao().upsertClass(studentClass)
                    getGrades(
                        sigaaSerializer.parseGradesRequestId(res),
                        idTurma,
                        cookie
                    )
                    getAttendance(
                        true,
                        sigaaSerializer.parseAttendanceRequestId(res),
                        idTurma,
                        cookie
                    )
                    getNews(
                        idTurma,
                        newsRequestId,
                        cookie
                    )
                    "Success"
                }
                else -> "Tempo de conexão expirou"
            }
        }
    }

    private suspend fun getGrades(requestId: String, idTurma: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            var status = "Tempo de conexão expirou"
            val formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add(requestId, requestId)
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/ava/index.jsf")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Cookie", "JSESSIONID=$cookie")
                .header("Referer", "https://si3.ufc.br/sigaa/ava/index.jsf")
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
                    saveViewState(res)
                    sigaaSerializer.parseGrades(idTurma, res).forEach {
                        studentDatabase.studentDao().upsertGrade(it)
                    }
                }
                else{
                    status = ""
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
        }
    }

    private suspend fun getAttendance(isPrevious: Boolean, requestId: String, idTurma: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            var status = "Tempo de conexão expirou"
            val formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add(requestId, requestId)
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/ava/index.jsf")
                .header("formMenu", "formMenu")
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
                    val attendance = sigaaSerializer.parseAttendance(res)
                    val studentClass = when(isPrevious){
                        true -> studentDatabase.studentDao().getPreviousClassWithIdTurmaAsync(idTurma)
                        false -> studentDatabase.studentDao().getClassWithIdTurmaAsync(idTurma)
                    }
                    studentClass.attendance = attendance.attended
                    studentClass.missed = attendance.missed
                    studentDatabase.studentDao().upsertClass(studentClass)
                }
                else{
                    println("erro")
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
        }
    }

    suspend fun getNews(idTurma: String, requestId: String, cookie: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            var status = "Tempo de conexão expirou"
            val formBody = FormBody.Builder()
                .add("formMenu", "formMenu")
                .add(requestId, requestId)
                .add("javax.faces.ViewState", viewState)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/ava/index.jsf")
                .header("formMenu", "formMenu")
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
                    val news = sigaaSerializer.parseNews(idTurma, res)
                    saveViewState(res)
                    studentDatabase.studentDao().deleteNews(idTurma)
                    news.forEach {
                        println(it)
                        studentDatabase.studentDao().insertNews(it)
                    }
                }
                else{
                    println("erro")
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
        }
    }

    suspend fun fetchNewsContent(cookie: String, id: String, requestId: String, requestId2: String){
        val viewState = getViewStateAsync().valueState
        withContext(Dispatchers.IO){
            val formBody = FormBody.Builder()
                .add(requestId, requestId)
                .add("javax.faces.ViewState", viewState)
                .add(requestId2, requestId2)
                .add("id", id)
                .build()
            val request = Request.Builder()
                .url("https://si3.ufc.br/sigaa/ava/NoticiaTurma/listar.jsf")
                .header("Cookie", "JSESSIONID=$cookie")
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
                    val content = sigaaSerializer.parseNewsContent(res)
                    val news = studentDatabase.studentDao().getNewsWithIdAsync(id)
                    news.content = content
                    studentDatabase.studentDao().upsertNewsContent(news)
                }
                else{
                    println("erro")
                }
            }
            catch(e: NoConnectivityException){
                Log.e("Connectivity", "No internet Connection.", e)
            }
            catch (e: SocketTimeoutException) {
                Log.e("Connectivity", "No internet Connection.", e)
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