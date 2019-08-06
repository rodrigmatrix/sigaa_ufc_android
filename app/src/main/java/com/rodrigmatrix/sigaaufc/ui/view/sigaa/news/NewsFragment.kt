package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class NewsFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: NewsViewModelFactory by instance()

    private lateinit var viewModel: NewsViewModel

    private lateinit var idTurma: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(NewsViewModel::class.java)
        idTurma = arguments?.getString("idTurma")!!
        bindNews(idTurma)
    }

    private fun bindNews(idTurma: String){
        launch(handler) {
            viewModel.deleteNews(idTurma)
            viewModel.getNews(idTurma).observe(this@NewsFragment, Observer {
                if(it == null) return@Observer
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
                    }
                }
            })
        }

    }

}
