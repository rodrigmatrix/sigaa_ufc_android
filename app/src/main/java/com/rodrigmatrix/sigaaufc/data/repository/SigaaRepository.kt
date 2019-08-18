package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.*


interface SigaaRepository {
    suspend fun login(
        cookie: String,
        login: String,
        password: String
    ): String

    suspend fun getStudent(): LiveData<out Student>

    suspend fun getStudentAsync(): Student

    suspend fun saveLogin(login: String, password: String)

    suspend fun getCookie(): Boolean

    suspend fun getHistoryRu(): LiveData<out MutableList<HistoryRU>>

    suspend fun getRuCard(): LiveData<out RuCard>

    suspend fun saveRuData(
        numeroCartao: String,
        matricula: String): String

    suspend fun getCurrentClasses(): MutableList<StudentClass>

    suspend fun setClass(id: String, idTurma: String)

    suspend fun fetchCurrentClasses()

    suspend fun getClass(idTurma: String): LiveData<out StudentClass>

    suspend fun getGrades(idTurma: String): LiveData<out MutableList<Grade>>

    suspend fun getIra(): LiveData<out MutableList<Ira>>

    suspend fun deleteGrades()

    suspend fun deleteNews(idTurma: String)

    suspend fun getNews(idTurma: String): LiveData<out MutableList<News>>

    suspend fun getNewsWithId(idNews: String): LiveData<out News>

    suspend fun insertFakeNews(idTurma: String)

    suspend fun fetchNews(newsId: String, requestId: String, requestId2: String)

    suspend fun fetchNewsPage(idTurma: String)

}