package com.rodrigmatrix.sigaaufc.ui.fragments

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room

import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.StudentDatabase
import com.rodrigmatrix.sigaaufc.ui.adapters.ClassesAdapter
import kotlinx.android.synthetic.main.fragment_classes.*


class ClassesFragment : Fragment() {
    private lateinit var database: StudentDatabase
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
            StudentDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        var classes = database.studentDao().getClasses()
        var previousClasses = database.studentDao().getPreviousClasses()
        recyclerView_classes.layoutManager = LinearLayoutManager(context)
        recyclerView_classes.adapter = ClassesAdapter(classes)
        if(classes.size == 0){
            no_class.isVisible = true
            recyclerView_classes.isVisible = false
        }
        else{
            no_class.isVisible = false
            recyclerView_classes.isVisible = true
        }
        switch_classes.setOnClickListener {
            switch_classes.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            when(switch_classes.isChecked){
                true -> {
                    if(previousClasses.size == 0){
                        no_class.isVisible = true
                        recyclerView_classes.isVisible = false
                    }
                    else{
                        no_class.isVisible = false
                        recyclerView_classes.isVisible = true
                        recyclerView_classes.layoutManager = LinearLayoutManager(context)
                        recyclerView_classes.adapter = ClassesAdapter(previousClasses)
                    }
                }
                false -> {
                    if(classes.size == 0){
                        no_class.isVisible = true
                        recyclerView_classes.isVisible = false
                    }
                    else{
                        no_class.isVisible = false
                        recyclerView_classes.isVisible = true
                        recyclerView_classes.layoutManager = LinearLayoutManager(context)
                        recyclerView_classes.adapter = ClassesAdapter(classes)
                    }

                }
            }
        }
    }

}
