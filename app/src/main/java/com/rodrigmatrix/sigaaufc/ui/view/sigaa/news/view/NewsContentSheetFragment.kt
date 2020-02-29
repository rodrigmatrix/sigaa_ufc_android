package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import kotlinx.android.synthetic.main.fragment_news_content.*
import kotlinx.coroutines.*
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.coroutines.CoroutineContext

class NewsContentSheetFragment : BottomSheetDialogFragment(), KodeinAware, CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext = job + Dispatchers.Main

    override val kodein by closestKodein()
    private val viewModelFactory: NewsContentViewModelFactory by instance()

    private lateinit var viewModel: NewsContentViewModel

    private lateinit var newsId: String
    private lateinit var turmaId: String
    private lateinit var requestId: String
    private lateinit var requestId2: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_content, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[NewsContentViewModel::class.java]
        newsId = arguments?.getString("idNews") ?: ""
        requestId = arguments?.getString("requestId") ?: ""
        requestId2 = arguments?.getString("requestId2") ?: ""
        turmaId = arguments?.getString("idTurma") ?: ""
        fetchContent()
    }

    private fun fetchContent(){
        launch(handler){
            //viewModel.fetchNewsPage(turmaId)
            viewModel.fetchNews(newsId, requestId, requestId2)
            bindUi()
        }
    }

    private fun bindUi(){
        launch(handler) {
            viewModel.getNews(newsId).observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                if(it.content != ""){
                    println(it)
                    runOnUiThread {
                        title_text.text = it.title
                        date_text.text = it.date
                        content_text.text = it.content
                        progress_news.isVisible = false
                        layout_content.isVisible = true
                    }
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }

    val handler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Exception", ":$throwable")
    }
}