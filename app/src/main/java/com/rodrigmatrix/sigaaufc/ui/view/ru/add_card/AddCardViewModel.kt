package com.rodrigmatrix.sigaaufc.ui.view.ru.add_card

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.RuCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddCardViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun fetchRu(numeroCartao: String, matricula: String): String{
        return this.sigaaRepository.saveRuData(numeroCartao, matricula)
    }

    suspend fun getRuCard(): LiveData<out RuCard> {
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getRuCard()
        }
    }
}