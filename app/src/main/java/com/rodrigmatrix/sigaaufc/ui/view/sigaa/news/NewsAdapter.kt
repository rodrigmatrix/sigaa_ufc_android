package com.rodrigmatrix.sigaaufc.ui.view.sigaa.news

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.News
import kotlinx.android.synthetic.main.news_row.view.*

class NewsAdapter(private val newsList: MutableList<News>): RecyclerView.Adapter<NewsViewHolder>(){
    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val iraRow = layoutInflater.inflate(R.layout.news_row, parent, false)
        return NewsViewHolder(iraRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.view.date_text.text = news.date
        holder.view.title_text.text = news.title
    }
}


class NewsViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.card_news.setOnClickListener {

        }
    }
}