package com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades

import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GradesViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun setClass(id: String, idTurma: String){
        withContext(Dispatchers.IO){
            sigaaRepository.setClass(id, idTurma)
        }
    }

}