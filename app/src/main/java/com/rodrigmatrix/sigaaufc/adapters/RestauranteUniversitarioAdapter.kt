//package com.rodrigmatrix.sigaaufc.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.rodrigmatrix.sigaaufc.R
//import com.rodrigmatrix.sigaaufc.persistence.HistoryRU
//
//class RestauranteUniversitarioAdapter(private val historyList: MutableList<HistoryRU>): RecyclerView.Adapter<RestauranteUniversitarioViewHolder>() {
//
//    override fun getItemCount(): Int {
//        return newsList.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteUniversitarioViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val notasRow = layoutInflater.inflate(R.layout.noticia_row, parent, false)
//        return RestauranteUniversitarioViewHolder(notasRow)
//    }
//
//    override fun onBindViewHolder(holder: RestauranteUniversitarioViewHolder, position: Int) {
//        val news = newsList[position]
//        holder.view.news_date.text = news.date
//        holder.view.news_content.text = news.content
//    }
//}
//
//class RestauranteUniversitarioViewHolder(val view: View): RecyclerView.ViewHolder(view){
//    init {
//
//
//    }
//}