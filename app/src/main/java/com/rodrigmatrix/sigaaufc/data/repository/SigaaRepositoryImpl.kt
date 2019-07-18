package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rodrigmatrix.sigaaufc.data.network.SigaaNetworkDataSource
import com.rodrigmatrix.sigaaufc.persistence.StudentDao
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    override suspend fun getCookie(): Boolean {
        return sigaaNetworkDataSource.getCookie()
    }

    override suspend fun saveLogin(login: String, password: String) {
        withContext(Dispatchers.IO){
            val student = studentDao.getStudent().value ?: return@withContext
            student.login = login
            student.password = password
            studentDao.upsertStudent(student)
        }
    }
}