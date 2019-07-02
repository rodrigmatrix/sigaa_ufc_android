package com.rodrigmatrix.sigaaufc.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.HistoryRU
import kotlinx.android.synthetic.main.ru_row.view.*

class RestauranteUniversitarioAdapter(private val historyList: MutableList<HistoryRU>): RecyclerView.Adapter<RestauranteUniversitarioViewHolder>() {

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteUniversitarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val ruRow = layoutInflater.inflate(R.layout.ru_row, parent, false)
        return RestauranteUniversitarioViewHolder(ruRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RestauranteUniversitarioViewHolder, position: Int) {
        val history = historyList[position]
        holder.view.operation_text.text = history.operation
        holder.view.content_text.text = history.content
        holder.view.date_text.text = "Data: ${history.date}"
        holder.view.time_text.text = "Hora: ${history.time}"
    }
}

class RestauranteUniversitarioViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}