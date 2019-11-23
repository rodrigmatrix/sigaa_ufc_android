package com.rodrigmatrix.sigaaufc.ui.view.sigaa.documents

import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class DocumentsViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {

    suspend fun getHistorico(){
        sigaaRepository.getHistorico()
    }

    suspend fun saveViewState(res: String?){
        sigaaRepository.saveViewState(res)
    }
}
