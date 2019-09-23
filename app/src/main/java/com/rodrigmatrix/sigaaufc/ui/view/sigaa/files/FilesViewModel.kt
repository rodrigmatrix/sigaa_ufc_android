package com.rodrigmatrix.sigaaufc.ui.view.sigaa.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.File

class FilesViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {


    suspend fun getFiles(idTurma: String): LiveData<out MutableList<File>>{
        return sigaaRepository.getFiles(idTurma)
    }

    suspend fun getCookie(): String{
        return sigaaRepository.getSessionCookie()
    }

    suspend fun deleteFiles(idTurma: String){
        return sigaaRepository.deleteFiles(idTurma)
    }
}
