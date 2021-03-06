package com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Grade

class GradesViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun fetchGrades(idTurma: String): LiveData<out MutableList<Grade>>{
        return sigaaRepository.getGrades(idTurma)
    }

    suspend fun deleteGrades(){
        return sigaaRepository.deleteGrades()
    }

}