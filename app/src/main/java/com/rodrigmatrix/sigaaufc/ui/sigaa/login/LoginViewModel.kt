package com.rodrigmatrix.sigaaufc.ui.sigaa.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
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
        password: String,
        comando: String): LiveData<out Student>
    {
        return sigaaRepository.login(cookie, login, password, comando)
    }
}