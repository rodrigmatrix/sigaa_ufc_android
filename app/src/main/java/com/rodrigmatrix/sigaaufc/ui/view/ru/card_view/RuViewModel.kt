package com.rodrigmatrix.sigaaufc.ui.view.ru.card_view

import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.internal.lazyDeferred

class RuViewModel(
    private val sigaaRepository: SigaaRepository
): ViewModel() {

    val student by lazyDeferred {
        sigaaRepository.getStudent()
    }

    val historyRu by lazyDeferred{
        sigaaRepository.getHistoryRu()
    }
}