package com.rodrigmatrix.sigaaufc.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.adapters.ClassesAdapter
import com.rodrigmatrix.sigaaufc.persistence.StudentsDatabase
import kotlinx.android.synthetic.main.fragment_classes.*


class ClassesFragment : Fragment() {
    private lateinit var database: StudentsDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Room.databaseBuilder(
            classes_fragment.context,
            StudentsDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var classes = database.studentDao().getClasses()
        recyclerView_classes.layoutManager = LinearLayoutManager(context)
        recyclerView_classes.adapter = ClassesAdapter(classes)
    }

}
