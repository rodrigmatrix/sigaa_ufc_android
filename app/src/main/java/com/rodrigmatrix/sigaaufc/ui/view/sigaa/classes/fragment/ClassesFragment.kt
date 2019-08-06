package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_classes.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClassesFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ClassesViewModelFactory by instance()

    private lateinit var viewModel: ClassesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(ClassesViewModel::class.java)
        launch(handler) {
            val classes = viewModel.getCurrentClasses()
            val previousClasses = viewModel.getCurrentClasses()
            println(classes)
            runOnUiThread {
                recyclerView_classes.layoutManager = LinearLayoutManager(context)
                recyclerView_classes.adapter =
                    CurrentClassesAdapter(
                        classes
                    )
                if(classes.size == 0){
                    no_class.isVisible = true
                    recyclerView_classes.isVisible = false
                }
                else{
                    no_class.isVisible = false
                    recyclerView_classes.isVisible = true
                }
//                switch_classes.setOnClickListener {
//                    onSwitchChange(classes, previousClasses)
//                }
            }
        }

    }


//    private fun onSwitchChange(classes: MutableList<StudentClass>, previousClasses: MutableList<StudentClass>){
//        switch_classes.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
//        when(switch_classes.isChecked){
//            true -> {
//                    if(previousClasses.size == 0){
//                        no_class.isVisible = true
//                        recyclerView_classes.isVisible = false
//                    }
//                    else{
//                        no_class.isVisible = false
//                        recyclerView_classes.isVisible = true
//                        recyclerView_classes.layoutManager = LinearLayoutManager(context)
//                        recyclerView_classes.adapter = CurrentClassesAdapter(previousClasses)
//                    }
//            }
//            false -> {
//                if(classes.size == 0){
//                    no_class.isVisible = true
//                    recyclerView_classes.isVisible = false
//                }
//                else{
//                    no_class.isVisible = false
//                    recyclerView_classes.isVisible = true
//                    recyclerView_classes.layoutManager = LinearLayoutManager(context)
//                    recyclerView_classes.adapter = CurrentClassesAdapter(classes)
//                }
//
//            }
//        }
//    }

}
