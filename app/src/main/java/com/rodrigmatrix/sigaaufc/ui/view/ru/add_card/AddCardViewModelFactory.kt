package com.rodrigmatrix.sigaaufc.ui.view.ru.add_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class AddCardViewModelFactory(
    private val sigaaRepository: SigaaRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddCardViewModel(sigaaRepository) as T
    }
}