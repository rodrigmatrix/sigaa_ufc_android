package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
import com.rodrigmatrix.sigaaufc.persistence.entity.RuCard
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass


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

    suspend fun fetchCurrentClasses(): String
}