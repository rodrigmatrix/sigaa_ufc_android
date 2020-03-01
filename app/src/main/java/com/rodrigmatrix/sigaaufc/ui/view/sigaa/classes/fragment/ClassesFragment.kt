package com.rodrigmatrix.sigaaufc.ui.view.sigaa.classes.fragment

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.rodrigmatrix.sigaaufc.R
import com.rodrigmatrix.sigaaufc.persistence.entity.StudentClass
import com.rodrigmatrix.sigaaufc.ui.base.ScopedFragment
import com.rodrigmatrix.sigaaufc.ui.view.ru.add_card.AddCardViewModel
import kotlinx.android.synthetic.main.fragment_classes.*
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.runOnUiThread
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class ClassesFragment : ScopedFragment(R.layout.fragment_classes) {

    private val viewModelFactory: ClassesViewModelFactory by instance()

    private lateinit var viewModel: ClassesViewModel
    private var fetched = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[ClassesViewModel::class.java]
        launch(handler) {
            val classes = viewModel.getCurrentClasses()
            viewModel.getPreviousClasses().observe(viewLifecycleOwner, Observer {
                if(it == null) return@Observer
                if(switch_classes.isChecked){
                    if(it.size == 0){
                        no_class.isVisible = true
                        recyclerView_previous_classes.isVisible = false
                    }
                    else{
                        try {
                            if(classes.size-1 > 0){
                                it.removeRange(IntRange(0, classes.size-1))
                            }
                        }catch(e: Exception){}
                        no_class.isVisible = false
                        recyclerView_previous_classes.isVisible = true
                        recyclerView_previous_classes.layoutManager = LinearLayoutManager(context)
                        recyclerView_previous_classes.adapter = PreviousClassesAdapter(it)
                        setRecycler(false)
                    }
                }
            })
            runOnUiThread {
                recyclerView_classes.layoutManager = LinearLayoutManager(context)
                recyclerView_classes.adapter = CurrentClassesAdapter(classes)
                if(classes.size == 0){
                    no_class.isVisible = true
                    recyclerView_classes.isVisible = false
                }
                else{
                    no_class.isVisible = false
                    recyclerView_classes.isVisible = true
                }
                switch_classes.setOnClickListener {
                   onSwitchChange(classes)
                }
            }

        }
    }

    private inline fun <reified T> MutableList<T>.removeRange(range: IntRange) {
        val fromIndex = range.first
        val toIndex = range.last
        if (fromIndex == toIndex) {
            return
        }

        if (fromIndex >= size) {
            throw Throwable("fromIndex $fromIndex >= size $size")
        }
        if (toIndex > size) {
            throw IndexOutOfBoundsException("toIndex $toIndex > size $size")
        }
        if (fromIndex > toIndex) {
            throw IndexOutOfBoundsException("fromIndex $fromIndex > toIndex $toIndex")
        }

        val filtered = filterIndexed { i, t -> i < fromIndex || i > toIndex }
        clear()
        addAll(filtered)
    }

    private fun onSwitchChange(classes: MutableList<StudentClass>){
        switch_classes.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        when(switch_classes.isChecked){
            true -> {
                    if(!fetched){
                        fetched = true
                        launch {
                            runOnUiThread {
                                progress_classes.visibility = View.VISIBLE
                            }
                            viewModel.fetchPreviousClasses()
                            runOnUiThread {
                                progress_classes.visibility = View.GONE
                            }
                        }
                    }
                    setRecycler(false)
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
                    recyclerView_classes.adapter = CurrentClassesAdapter(classes)
                }
                setRecycler(true)
            }
        }
    }

    private fun setRecycler(current: Boolean){
        if(current){
            recyclerView_classes.visibility = View.VISIBLE
            recyclerView_previous_classes.visibility = View.GONE
        }
        else{
            recyclerView_classes.visibility = View.GONE
            recyclerView_previous_classes.visibility = View.VISIBLE
        }
    }

}
