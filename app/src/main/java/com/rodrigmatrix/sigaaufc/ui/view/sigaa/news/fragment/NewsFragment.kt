package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NewsFragment : ScopedFragment(R.layout.fragment_news), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: NewsViewModelFactory by instance()

    private lateinit var viewModel: NewsViewModel

    private lateinit var idTurma: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]
        idTurma = arguments?.getString("idTurma")!!
        bindNews(idTurma)
    }

    private fun bindNews(idTurma: String){
        launch(handler) {
            viewModel.deleteNews(idTurma)
            viewModel.insertFakeNews(idTurma)
            viewModel.getNews(idTurma).observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                if((it.size == 1) && (it[0].requestId == "fake")){
                    return@Observer
                }
                if(it.size == 0){
                    runOnUiThread {
                        empty_news_view.isVisible = true
                        recycler_view_news.isVisible = false
                    }
                }
                else{
                    runOnUiThread {
                        empty_news_view.isVisible = false
                        recycler_view_news.isVisible = true
                        recycler_view_news.layoutManager = LinearLayoutManager(context)
                        recycler_view_news.adapter = NewsAdapter(this@NewsFragment, it, idTurma)
                    }
                }
            })
        }

    }

}
