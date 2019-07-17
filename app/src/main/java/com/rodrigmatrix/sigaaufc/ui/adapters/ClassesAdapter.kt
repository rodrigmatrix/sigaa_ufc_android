package com.rodrigmatrix.sigaaufc.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodrigmatrix.sigaaufc.ui.activities.ClassActivity
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import kotlinx.android.synthetic.main.class_row.view.*

class ClassesAdapter(private val classesList: MutableList<StudentClass>): RecyclerView.Adapter<ClassesViewHolder>() {

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
        holder.view.code_text.text = "Código: ${classElement.code}"
        holder.view.idTurma_text.text = classElement.turmaId
        holder.view.id_text.text = classElement.id.toString()
        when {
            classElement.credits == "" -> {
                holder.view.credits_text.visibility = View.GONE
            }
            else -> {
                holder.view.credits_text.text = "Créditos: ${classElement.credits}"
            }
        }
        holder.view.period_text.text = classElement.days
    }
}

class ClassesViewHolder(val view: View): RecyclerView.ViewHolder(view){
    init {
        view.open_class_btn.setOnClickListener {
            val intent = Intent(view.context, ClassActivity::class.java)
            intent.putExtra("idTurma", view.idTurma_text.text.toString())
            intent.putExtra("id", view.id_text.text.toString())
            view.context.startActivity(intent)
        }
    }
}