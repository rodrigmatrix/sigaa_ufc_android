package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.selected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class ClassViewModelFactory(
    private val sigaaRepository: SigaaRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClassViewModel(sigaaRepository) as T
    }
}