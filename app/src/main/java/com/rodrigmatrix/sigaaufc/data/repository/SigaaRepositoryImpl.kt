package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.data.network.SigaaNetworkDataSource
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SigaaRepositoryImpl(
    private val studentDao: StudentDao,
    private val sigaaNetworkDataSource: SigaaNetworkDataSource
) : SigaaRepository {

    override suspend fun login(
        cookie: String,
        login: String,
        password: String
    ): String {
        return sigaaNetworkDataSource.fetchLogin(cookie, login, password)
    }

    override suspend fun getStudent(): LiveData<out Student> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getStudent()
        }
    }

    override suspend fun getStudentAsync(): Student {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getStudentAsync()
        }
    }

    override suspend fun getCookie(): Boolean {
        return sigaaNetworkDataSource.getCookie()
    }

    override suspend fun saveLogin(login: String, password: String) {
        withContext(Dispatchers.IO){
            val student = getStudentAsync()
            student.login = login
            student.password = password
            studentDao.upsertStudent(student)
        }
    }

    override suspend fun getHistoryRu(): LiveData<out MutableList<HistoryRU>> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getHistoryRU()
        }
    }

    override suspend fun saveRuData(numeroCartao: String, matricula: String): String {
        var status = ""
        withContext(Dispatchers.IO){
            val triple = fetchRuCard(numeroCartao, matricula)
            if(triple.first == "Success"){
                triple.second.forEach {
                    studentDao.insertRU(it)
                }
                val ruCard = RuCard(triple.third.second,
                    triple.third.first,
                    matricula,
                    numeroCartao)
                println("salvou")
                studentDao.upsertRuCard(ruCard)
            }
            status = triple.first
        }
        return status
    }

    override suspend fun getRuCard(): LiveData<out RuCard> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getRuCard()
        }
    }

    private suspend fun fetchRuCard(numeroCartao: String, matricula: String): Triple<String, MutableList<HistoryRU>, Pair<String, Int>>{
        return sigaaNetworkDataSource.fetchRu(numeroCartao, matricula)
    }

    override suspend fun getCurrentClasses(): MutableList<StudentClass> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getClasses()
        }
    }

    override suspend fun setClass(id: String, idTurma: String) {
        val cookie = studentDao.getStudentAsync().jsession
        return sigaaNetworkDataSource.fetchClass(id, idTurma, cookie)
    }

    override suspend fun fetchCurrentClasses() {
        val student = getStudentAsync()
        val cookie = student.jsession
        val login = student.login
        val password = student.password
        sigaaNetworkDataSource.fetchCurrentClasses(cookie)
    }

    override suspend fun getClass(idTurma: String): LiveData<out StudentClass> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getClassWithIdTurma(idTurma)
        }
    }

    override suspend fun getGrades(idTurma: String): LiveData<out MutableList<Grade>> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getGrades(idTurma)
        }
    }

    override suspend fun getIra(): LiveData<out MutableList<Ira>> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getIra()
        }
    }

    override suspend fun deleteGrades() {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.deleteGrades()
        }
    }

    override suspend fun deleteNews(idTurma: String) {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.deleteNews(idTurma)
        }
    }

    override suspend fun getNews(idTurma: String): LiveData<out MutableList<News>> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getNews(idTurma)
        }
    }

    override suspend fun insertFakeNews(idTurma: String) {
        withContext(Dispatchers.IO){
            studentDao.insertNews(News( "","fake", "", idTurma, "", "", ""))
        }
    }

    override suspend fun fetchNews(newsId: String, requestId: String, requestId2: String) {
        val student = getStudentAsync()
        val cookie = student.jsession
        sigaaNetworkDataSource.fetchNews(cookie, newsId, requestId, requestId2)
    }

    override suspend fun getNewsWithId(idNews: String): LiveData<out News> {
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getNewsWithId(idNews)
        }
    }


}