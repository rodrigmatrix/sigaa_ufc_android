package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClassViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun fetchCurrentClasses(): String{
        return sigaaRepository.fetchCurrentClasses()
    }

    suspend fun setClass(id: String, idTurma: String){
        withContext(Dispatchers.IO){
            sigaaRepository.setClass(id, idTurma)
        }
    }
}