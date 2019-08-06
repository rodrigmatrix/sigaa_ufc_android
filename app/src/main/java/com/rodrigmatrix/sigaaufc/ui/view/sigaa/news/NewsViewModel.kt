package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.News

class NewsViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {

    suspend fun deleteNews(idTurma: String){
        sigaaRepository.deleteNews(idTurma)
    }


    suspend fun getNews(idTurma: String): LiveData<out MutableList<News>> {
        return sigaaRepository.getNews(idTurma)
    }
}
