package com.rodrigmatrix.sigaaufc.ui.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivityViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun getStudent(): LiveData<out Student> {
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getStudent()
        }
    }

}