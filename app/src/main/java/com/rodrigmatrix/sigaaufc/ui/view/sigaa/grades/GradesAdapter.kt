package com.rodrigmatrix.sigaaufc.ui.view.sigaa.grades

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.Grade
import kotlinx.android.synthetic.main.grade_row.view.*

class GradesAdapter(private val gradesList: MutableList<Grade>): RecyclerView.Adapter<GradesViewHolder>() {

    override fun getItemCount(): Int {
        return gradesList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gradeRow = layoutInflater.inflate(R.layout.grade_row, parent, false)
        return GradesViewHolder(gradeRow)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GradesViewHolder, position: Int) {
        val grade = gradesList[position]
        holder.view.name_grade_text.text = grade.name
        if(grade.content == ""){
            holder.view.grade_text.text = "Não cadastrado"
        }
        else{
            holder.view.grade_text.text = grade.content
        }

    }
}

class GradesViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
    }
}