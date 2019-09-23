package com.rodrigmatrix.sigaaufc.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class MainActivityViewModelFactory(
    private val sigaaRepository: SigaaRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(sigaaRepository) as T
    }
}