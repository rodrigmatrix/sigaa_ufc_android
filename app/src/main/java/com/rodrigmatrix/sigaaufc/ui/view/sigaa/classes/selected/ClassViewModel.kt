package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClassViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun fetchCurrentClasses(){
        return sigaaRepository.fetchCurrentClasses()
    }

    suspend fun getCurrentClass(idTurma: String): LiveData<out StudentClass> {
        return sigaaRepository.getClass(idTurma)
    }

    suspend fun getPreviousClass(idTurma: String): LiveData<out StudentClass> {
        return sigaaRepository.getPreviousClass(idTurma)
    }

    suspend fun fetchPreviousClasses(){
        return sigaaRepository.fetchPreviousClasses()
    }



    suspend fun setClass(id: String, idTurma: String){
        withContext(Dispatchers.IO){
            sigaaRepository.setClass(id, idTurma)
        }
    }

    suspend fun setPreviousClass(id: String, idTurma: String){
        withContext(Dispatchers.IO){
            sigaaRepository.setPreviousClass(id, idTurma)
        }
    }


}