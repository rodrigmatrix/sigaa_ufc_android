package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository

class NewsViewModelFactory(
    private val sigaaRepository: SigaaRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(sigaaRepository) as T
    }
}