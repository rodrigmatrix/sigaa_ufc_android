package com.rodrigmatrix.sigaaufc.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

//    init {
//        sigaaNetworkDataSource.downloadedLogin.observeForever {
//            persistLoginData(it)
//        }
//    }

    override suspend fun login(
        cookie: String,
        login: String,
        password: String,
        comando: String
    ): LiveData<out Student> {
        sigaaNetworkDataSource.fetchLogin(cookie, login, password, comando)
        return withContext(Dispatchers.IO){
            return@withContext studentDao.getStudent()
        }
    }

    private fun persistLoginData(fetchedLogin: String){
        GlobalScope.launch(Dispatchers.IO) {
            println("save login")
//            currentWeatherDao.upsert(fetchedWeather.current)
//            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }
}