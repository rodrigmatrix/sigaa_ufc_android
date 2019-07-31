package com.rodrigmatrix.sigaaufc.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.HistoryRU
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
        when(history.content){
            "Refeição: Jantar" -> {
                holder.view.image_ru.setImageResource(R.drawable.dinner)
                holder.view.total_text.text = history.content
                holder.view.date_text.text = "Data: ${history.date}"
                holder.view.time_text.text = "Hora: ${history.time}"
            }
            "Refeição: Almoço" -> {
                holder.view.image_ru.setImageResource(R.drawable.lunch)
                holder.view.total_text.text = history.content
                holder.view.date_text.text = "Data: ${history.date}"
                holder.view.time_text.text = "Hora: ${history.time}"
            }

            else -> {
                if(history.content.contains("Qtd. Créditos")){
                    var before = history.content.split("Antes: ", "/")
                    var after = history.content.split("Depois: ")
                    holder.view.image_ru.setImageResource(R.drawable.ic_credit_card)
                    holder.view.total_text.text = "Recarga do cartão"
                    holder.view.date_text.text = "Depois: ${after[after.size-1]}"
                    holder.view.time_text.text = "Antes: ${before[1]}"
                }
                else{
                    holder.view.image_ru.setImageResource(R.drawable.coffee)
                    holder.view.total_text.text = history.content
                    holder.view.date_text.text = "Data: ${history.date}"
                    holder.view.time_text.text = "Hora: ${history.time}"
                }
            }
        }
    }
}

class RestauranteUniversitarioViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}