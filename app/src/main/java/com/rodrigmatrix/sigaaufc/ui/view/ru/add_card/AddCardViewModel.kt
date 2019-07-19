package com.rodrigmatrix.sigaaufc.ui.view.ru.add_card

import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class AddCardViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun fetchRu(numeroCartao: String, matricula: String): String{
        return sigaaRepository.saveRuData(numeroCartao, matricula)
    }
}