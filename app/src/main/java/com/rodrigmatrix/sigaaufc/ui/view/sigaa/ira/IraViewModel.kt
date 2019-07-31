package com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.Ira

class IraViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {


    suspend fun getIra(): LiveData<out MutableList<Ira>>{
        return sigaaRepository.getIra()
    }
}
