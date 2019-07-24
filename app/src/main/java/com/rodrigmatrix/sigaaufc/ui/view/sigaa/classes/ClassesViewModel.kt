package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Student
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClassesViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {


    suspend fun getCookie(): Boolean{
        return sigaaRepository.getCookie()
    }

    suspend fun getCurrentClasses(): MutableList<StudentClass>{
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getCurrentClasses()
        }
    }

    suspend fun getStudent(): LiveData<out Student> {
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getStudent()
        }
    }

    suspend fun getStudentAsync(): Student {
        return withContext(Dispatchers.IO){
            return@withContext sigaaRepository.getStudentAsync()
        }
    }
}