package com.rodrigmatrix.sigaaufc.ui.sigaa.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.lazyDeferred
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

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
        withContext(Dispatchers.IO){
            sigaaRepository.saveLogin(login, password)
        }
    }

    suspend fun getCookie(): Boolean{
        return sigaaRepository.getCookie()
    }

    val student by lazyDeferred {
        sigaaRepository.getStudent()
    }
}