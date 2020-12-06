package com.rodrigmatrix.sigaaufc.ui.view.ru.card_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.lazyDeferred
import com.rodrigmatrix.sigaaufc.persistence.entity.RuCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RuViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    suspend fun getRuCard(): LiveData<out RuCard> {
        return withContext(Dispatchers.IO) {
            return@withContext sigaaRepository.getRuCard()
        }
    }

    suspend fun fetchRuData(numeroCartao: String, matricula: String){
        sigaaRepository.saveRuData(numeroCartao, matricula)
    }

    val historyRu by lazyDeferred{
        sigaaRepository.getHistoryRu()
    }
}
