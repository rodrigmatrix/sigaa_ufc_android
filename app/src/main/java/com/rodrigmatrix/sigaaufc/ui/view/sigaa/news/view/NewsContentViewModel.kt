package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rodrigmatrix.sigaaufc.data.repository.SigaaRepository
import com.rodrigmatrix.sigaaufc.persistence.entity.News

class NewsContentViewModel(
    private val sigaaRepository: SigaaRepository
) : ViewModel() {

    suspend fun fetchNewsPage(idTurma: String){
        sigaaRepository.fetchNewsPage(idTurma)
    }

    suspend fun fetchNews(newsId: String, requestId: String, requestId2: String){
        sigaaRepository.fetchNews(newsId, requestId, requestId2)
    }


    suspend fun getNews(idTurma: String): LiveData<out News> {
        return sigaaRepository.getNewsWithId(idTurma)
    }
}