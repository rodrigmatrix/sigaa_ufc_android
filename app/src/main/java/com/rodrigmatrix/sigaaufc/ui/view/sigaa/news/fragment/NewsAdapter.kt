package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.News
import com.rodrigmatrix.sigaaufc.ui.view.sigaa.news.view.NewsContentSheetFragment
import kotlinx.android.synthetic.main.news_row.view.*

class NewsAdapter(
    private val newsFragment: NewsFragment,
    private val newsList: MutableList<News>,
    private val idTurma: String
): RecyclerView.Adapter<NewsViewHolder>(){

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val iraRow = layoutInflater.inflate(R.layout.news_row, parent, false)
        return NewsViewHolder(newsFragment, iraRow, idTurma)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.view.date_text.text = news.date
        holder.view.title_text.text = news.title
        holder.view.id_news.text = news.newsId
        holder.view.requestId_news.text = news.requestId
        holder.view.requestId2_news.text = news.requestId2
    }
}


class NewsViewHolder(
    private val newsFragment: NewsFragment,
    val view: View,
    private val idTurma: String
): RecyclerView.ViewHolder(view){
    init {
        view.card_news.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("idNews", view.id_news.text.toString())
            bundle.putString("requestId", view.requestId_news.text.toString())
            bundle.putString("requestId2", view.requestId2_news.text.toString())
            bundle.putString("idTurma", idTurma)
            val newsContentSheetFragment = NewsContentSheetFragment()
            newsContentSheetFragment.arguments = bundle
            newsContentSheetFragment.show(newsFragment.fragmentManager!!, "content_news")
        }
    }
}