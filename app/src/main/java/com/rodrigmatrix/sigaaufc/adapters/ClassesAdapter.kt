package com.rodrigmatrix.sigaaufc.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.Class
import kotlinx.android.synthetic.main.class_row.view.*

class ClassesAdapter(private val classesList: MutableList<Class>): RecyclerView.Adapter<ClassesViewHolder>() {

    override fun getItemCount(): Int {
        return classesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val ruRow = layoutInflater.inflate(R.layout.class_row, parent, false)
        return ClassesViewHolder(ruRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ClassesViewHolder, position: Int) {
        val classElement = classesList[position]
        holder.view.name_text.text = classElement.name

    }
}

class ClassesViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {


    }
}