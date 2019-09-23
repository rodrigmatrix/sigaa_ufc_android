package com.rodrigmatrix.sigaaufc.ui.view.sigaa.ira

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.Ira
import kotlinx.android.synthetic.main.ira_row.view.*

class IraAdapter(private val iraList: MutableList<Ira>): RecyclerView.Adapter<IraViewHolder>(){
    override fun getItemCount(): Int {
        return iraList.size/2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IraViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val iraRow = layoutInflater.inflate(R.layout.ira_row, parent, false)
        return IraViewHolder(iraRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IraViewHolder, position: Int) {
        val ira = iraList[position]
        holder.view.period_text.text = ira.period
        holder.view.ira_i_text.text = "Ira individual: ${ira.iraI}"
        holder.view.ira_g_text.text = "Ira Geral: ${ira.iraG}"
    }
}


class IraViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {

    }
}