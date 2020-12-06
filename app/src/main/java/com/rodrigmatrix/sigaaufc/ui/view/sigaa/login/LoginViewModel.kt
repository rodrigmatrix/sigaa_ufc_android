package com.rodrigmatrix.sigaaufc.ui.view.sigaa.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.Vinculo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {


    suspend fun login(
        cookie: String,
        login: String,
        password: String): String
    {
        return sigaaRepository.login(cookie, login, password)
    }

    suspend fun saveLogin(login: String, password: String){
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.saveLogin(login, password)
        }
    }

    suspend fun getCookie(): Boolean{
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.getCookie()
        }
    }

    suspend fun getStudent(): LiveData<out Student> {
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getStudent()
        }
    }

    suspend fun getStudentAsync(): Student{
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.getStudentAsync()
        }
    }

    suspend fun getVinculos(): MutableList<Vinculo>{
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.getVinculos()
        }
    }

    suspend fun setVinculo(vinculo: String){
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.setVinculo(vinculo)
        }
    }
}